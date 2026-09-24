# Copyright 2026 Broadcom. All Rights Reserved.

.PHONY: venv proto build run clean

# Pinned exactly so buf's local "python" built-in (which shells out to `protoc`) always
# produces gencode compatible with the `protobuf` version pinned in requirements.txt. An
# unpinned system protoc (e.g. from `brew install grpc`) can outpace every published
# protobuf PyPI release and fail at import time with a VersionError.
PROTOC_VERSION := 29.3
PROTOC_DIR     := .tools/protoc-$(PROTOC_VERSION)
PROTOC         := $(PROTOC_DIR)/bin/protoc

UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)
ifeq ($(UNAME_S),Darwin)
	ifeq ($(UNAME_M),arm64)
		PROTOC_PLATFORM := osx-aarch_64
	else
		PROTOC_PLATFORM := osx-x86_64
	endif
else
	ifeq ($(UNAME_M),aarch64)
		PROTOC_PLATFORM := linux-aarch_64
	else
		PROTOC_PLATFORM := linux-x86_64
	endif
endif

$(PROTOC):
	@echo "Downloading pinned protoc v$(PROTOC_VERSION) ($(PROTOC_PLATFORM))..."
	@mkdir -p $(PROTOC_DIR)
	@curl -sSL -o $(PROTOC_DIR)/protoc.zip https://github.com/protocolbuffers/protobuf/releases/download/v$(PROTOC_VERSION)/protoc-$(PROTOC_VERSION)-$(PROTOC_PLATFORM).zip
	@unzip -q -o $(PROTOC_DIR)/protoc.zip -d $(PROTOC_DIR)
	@rm $(PROTOC_DIR)/protoc.zip

venv:
	@if [ ! -f ".venv/.deps-installed" ]; then \
		set -e; \
		echo "Creating virtual environment..."; \
		rm -rf .venv; \
		python3 -m venv .venv; \
		.venv/bin/pip install --upgrade pip; \
		.venv/bin/pip install -r requirements.txt; \
		touch .venv/.deps-installed; \
	fi

proto: venv $(PROTOC)
	@if [ ! -f ../proto/gemfire/v1/gemfire.proto ]; then \
		echo "ERROR: ../proto/gemfire/v1/gemfire.proto not found."; \
		echo "Please download the VMware Tanzu GemFire gRPC Extension .tgz artifact, extract gemfire.proto, and place it in the ../proto/gemfire/v1/ directory."; \
		exit 1; \
	fi
	@echo "Generating Python code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@PATH="$(CURDIR)/$(PROTOC_DIR)/bin:$$PATH" buf generate ../proto --path ../proto/gemfire/v1 --path ../proto/test/v1
	@touch gemfire/__init__.py gemfire/v1/__init__.py
	@touch test/__init__.py test/v1/__init__.py

build: proto

run: build
	@echo "Running Python client..."
	@.venv/bin/python main.py

clean:
	@echo "Cleaning..."
	@rm -rf gemfire test .venv
	@find . -type d -name "__pycache__" -exec rm -rf {} +
