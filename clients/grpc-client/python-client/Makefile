# Copyright 2026 Broadcom. All Rights Reserved.

.PHONY: venv proto build run clean

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

proto: venv
	@if [ ! -f ../proto/gemfire/v1/gemfire.proto ]; then \
		echo "ERROR: ../proto/gemfire/v1/gemfire.proto not found."; \
		echo "Please download the VMware Tanzu GemFire gRPC Extension .tgz artifact, extract gemfire.proto, and place it in the ../proto/gemfire/v1/ directory."; \
		exit 1; \
	fi
	@echo "Generating Python code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@buf generate ../proto --path ../proto/gemfire/v1 --path ../proto/test/v1
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
