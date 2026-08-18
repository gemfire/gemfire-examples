.PHONY: proto build run integration-test clean

proto:
	@if [ ! -f ../proto/gemfire/v1/gemfire.proto ]; then \
		echo "ERROR: ../proto/gemfire/v1/gemfire.proto not found."; \
		echo "Please download the VMware Tanzu GemFire gRPC Extension .tgz artifact, extract gemfire.proto, and place it in the ../proto/gemfire/v1/ directory."; \
		exit 1; \
	fi
	@if [ ! -f ../proto/google/api/field_behavior.proto ]; then \
		echo "Downloading google/api/field_behavior.proto..."; \
		mkdir -p ../proto/google/api; \
		curl -sSL https://raw.githubusercontent.com/googleapis/googleapis/master/google/api/field_behavior.proto -o ../proto/google/api/field_behavior.proto; \
	fi
	@echo "Generating Go code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@protoc --go_out=gemfire/v1 --go_opt=paths=source_relative \
		--go-grpc_out=gemfire/v1 --go-grpc_opt=paths=source_relative \
		--proto_path=../proto/gemfire/v1 \
		--proto_path=../proto \
		../proto/gemfire/v1/gemfire.proto
	@protoc --go_out=test/v1 --go_opt=paths=source_relative \
		--go-grpc_out=test/v1 --go-grpc_opt=paths=source_relative \
		--proto_path=../proto/test/v1 \
		../proto/test/v1/person.proto

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

