# GemFire gRPC Client Examples

Example client applications demonstrating how to connect to and interact with the VMware Tanzu GemFire gRPC extension.

| Module | Language | Description |
|---|---|---|
| [`go-client/`](go-client/README.md) | Go | Go gRPC client exercising Put, Get, and error-handling scenarios |

The client targets the `CacheService` API and can be pointed at any running server — a GemFire cluster with the VMware Tanzu GemFire gRPC Extension installed.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire installation directory.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
- **Protocol Buffers Compiler**: `protoc` must be installed. ([Install protoc](https://protobuf.dev/installation/))
- **OpenSSL**: Required if you want to run the examples with TLS enabled (`--tls`). ([Install OpenSSL](https://openssl-library.org/source/))
- **Go**: 1.26 or later (required for the Go client). ([Install Go](https://go.dev/doc/install))
- **Go Plugins for protoc**:  (required for the Go client).
  ```bash
  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
  ```
- **GemFire gRPC Protobuf Definition**: You must extract `gemfire.proto` from the shipped `.tgz` artifact and place it in the `proto/gemfire/v1/` directory before building the client.

## Running End-to-End

To verify the client end to end against a real local cluster with no manual setup, run its `run-<client>.sh` script. This requires `GEMFIRE_HOME` to be set to a GemFire installation that has the gRPC extension `.gfm` file installed in its `extensions/` directory (or specified via `GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).

You must also extract `gemfire.proto` from the shipped `.tgz` artifact and place it in the `proto/gemfire/v1/` directory before building the client.

```bash
./go-client/run-go-client.sh
```

See the client's README for the manual steps this automates.
