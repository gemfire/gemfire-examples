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
	@buf generate ../proto --path ../proto/gemfire/v1 --template buf.gen.gemfire.yaml
	@buf generate ../proto --path ../proto/test/v1 --template buf.gen.person.yaml

build: proto
	@echo "Building Go client..."
	@go build -o gemfire-client main.go

run: build
	@echo "Running Go client..."
	@./gemfire-client

clean:
	@echo "Cleaning..."
	@rm -rf gemfire gemfire-client
	@go clean

