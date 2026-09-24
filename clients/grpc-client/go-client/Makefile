# Copyright 2026 Broadcom. All Rights Reserved.

.PHONY: proto build run integration-test clean

proto:
	@if [ ! -f ../proto/gemfire/v1/gemfire.proto ]; then \
		echo "ERROR: ../proto/gemfire/v1/gemfire.proto not found."; \
		echo "Please download the VMware Tanzu GemFire gRPC Extension .tgz artifact, extract gemfire.proto, and place it in the ../proto/gemfire/v1/ directory."; \
		exit 1; \
	fi
	@echo "Generating Go code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@buf generate ../proto --path ../proto/gemfire/v1 --path ../proto/test/v1 --template buf.gen.yaml

build: proto
	@echo "Building Go client..."
	@go build -o cache-client main.go

run: build
	@echo "Running Go client..."
	@./cache-client

clean:
	@echo "Cleaning..."
	@rm -rf gemfire cache-client
	@go clean

