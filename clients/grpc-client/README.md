# GemFire gRPC Client Examples

Example client applications demonstrating how to connect to and interact with the VMware Tanzu GemFire gRPC extension.

| Module | Language | Description |
|---|---|---|
| [`go-client/`](go-client/README.md) | Go | Go gRPC client exercising Put, Get, and error-handling scenarios |
| [`python-client/`](python-client/README.md) | Python | Python gRPC client exercising Put, Get, and error-handling scenarios |
| [`interop/`](interop/README.md) | Go + Java | Interop between the GemFire API and the gRPC API: a Go gRPC app writes Protobuf messages, a server-side cache listener reads their fields as they arrive, and a Java GemFire client reads the same entries back as Protobuf objects |

The client targets the `CacheService` API and can be pointed at any running server — a GemFire cluster with the VMware Tanzu GemFire gRPC Extension installed.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire installation directory.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
- **Make**: Required to run the build scripts. (Mac: `xcode-select --install`, Linux: `sudo apt install make`).
- **Protocol Buffers Compiler**: `protoc` must be installed. ([Install protoc](https://protobuf.dev/installation/))
- **OpenSSL**: Required if you want to run the examples with TLS enabled (`--tls`). ([Install OpenSSL](https://openssl-library.org/source/))
- **Go**: 1.26 or later (required for the Go client). ([Install Go](https://go.dev/doc/install))
- **Go Plugins for protoc**:  (required for the Go client).
  ```bash
  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
  ```
- **Python**: 3.8 or later (required for the Python client). ([Install Python](https://www.python.org/downloads/))
- **Python Tools**: `pip` and `venv` must be available (required for the Python client). These are usually bundled with Python, but on Linux you may need to install them (e.g., `sudo apt install python3-pip python3-venv`).
- **JDK 17** (required for the interop example's GemFire client and deployed listener; its gRPC client is Go).
- **GemFire gRPC Protobuf Definition**: Download the VMware Tanzu GemFire gRPC Extension `.tgz` artifact. Extract the `gemfire.proto` file from the archive and place it in the `../proto/gemfire/v1/` directory relative to this app (e.g., `clients/grpc-client/proto/gemfire/v1/gemfire.proto`).

## Running End-to-End

For `go-client` and `python-client`, run the client's `run-<client>.sh` script from its own directory. It starts a throwaway local cluster, runs the client against it, and shuts everything down.

```bash
./run-<client>.sh
```

See the client's README for the manual steps this automates.

The `interop` example is driven by `gfsh` scripts instead, because it pairs two applications against one cluster. See its [README](interop/README.md) for the numbered steps.
