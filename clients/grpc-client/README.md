# GemFire gRPC Client Examples

Example client applications demonstrating how to connect to and interact with the VMware Tanzu GemFire gRPC extension.

| Module | Language | Description |
|---|---|---|
| [`go-client/`](go-client/README.md) | Go | Go gRPC client exercising Put, Get, Query, and error-handling scenarios |
| [`python-client/`](python-client/README.md) | Python | Python gRPC client exercising Put, Get, Query, and error-handling scenarios |

The client targets the `CacheService` API and can be pointed at any running server — a GemFire cluster with the VMware Tanzu GemFire gRPC Extension installed.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire installation directory.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
- **Make**: Required to run the build scripts. (Mac: `xcode-select --install`, Linux: `sudo apt install make`).
- **Buf**: Used to generate code from proto files. ([Install Buf](https://buf.build/docs/installation))
- **OpenSSL**: Required if you want to run the examples with TLS enabled (`--tls`). ([Install OpenSSL](https://openssl-library.org/source/))
- **Go**: 1.26 or later (required for the Go client). ([Install Go](https://go.dev/doc/install))
- **Go Plugins for Buf**: (required for the Go client).
  ```bash
  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
  ```
- **Python**: 3.8 or later (required for the Python client). ([Install Python](https://www.python.org/downloads/))
- **Python Plugin for Buf**: `grpc_python_plugin` on `PATH` (required for the Python client). `protoc` itself is downloaded automatically, pinned to a version compatible with `python-client/requirements.txt`.
  ```bash
  brew install grpc  # macOS
  sudo apt install protobuf-compiler-grpc  # Linux
  ```
- **Python Tools**: `pip` and `venv` must be available (required for the Python client). These are usually bundled with Python, but on Linux you may need to install them (e.g., `sudo apt install python3-pip python3-venv`).
- **GemFire gRPC Protobuf Definition**: Download the VMware Tanzu GemFire gRPC Extension `.tgz` artifact. Extract the `gemfire.proto` file from the archive and place it in the `../proto/gemfire/v1/` directory relative to this app (e.g., `clients/grpc-client/proto/gemfire/v1/gemfire.proto`).

## Running End-to-End

To verify the client end to end against a real local cluster with no manual setup. From the `clients/grpc-client/<client>` directory, run its `run-<client>.sh` script.

```bash
./run-<client>.sh
```

See the client's README for the manual steps this automates.
