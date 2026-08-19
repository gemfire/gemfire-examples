.PHONY: venv proto build run clean

venv:
	@if [ ! -d ".venv" ]; then \
		echo "Creating virtual environment..."; \
		python3 -m venv .venv; \
		.venv/bin/pip install --upgrade pip; \
		.venv/bin/pip install -r requirements.txt; \
	fi

proto: venv
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
	@echo "Generating Python code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@.venv/bin/python -m grpc_tools.protoc -I../proto/gemfire/v1 -I../proto --python_out=gemfire/v1 --grpc_python_out=gemfire/v1 ../proto/gemfire/v1/gemfire.proto
	@.venv/bin/python -m grpc_tools.protoc -I../proto/test/v1 --python_out=test/v1 --grpc_python_out=test/v1 ../proto/test/v1/person.proto
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
