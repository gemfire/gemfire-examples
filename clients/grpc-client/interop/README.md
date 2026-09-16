
# GemFire gRPC API Interop Example

Interop between the GemFire API and the gRPC API: a Go gRPC app writes Protobuf messages, a
server-side cache listener reads their fields as they arrive, and a Java GemFire client reads the
same entries back as Protobuf objects and writes one of its own.

The two sides are deliberately in different languages. GemFire has no native Go client, and a
Java gRPC client would prove less: the point is that the stored value is readable through either
API, whatever wrote it.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire 10.3.2 or newer installation directory; older lines have no PROTOBUF DSCODE. Put `$GEMFIRE_HOME/bin` on your `PATH` as well, since the steps below call `gfsh` directly.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`). It must be built against the same GemFire line as the installation, because it links GemFire internals — mixing lines fails at runtime with `NoClassDefFoundError`, not at build time.
- [JDK](https://adoptium.net/) 17 or later
- **Go**: 1.26 or later (required for the Go client). ([Install Go](https://go.dev/doc/install))
- **Protocol Buffers Compiler**: `protoc` must be installed. ([Install protoc](https://protobuf.dev/installation/))
- **Go Plugins for protoc**: (required for the Go client).
  ```bash
  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
  ```
- **Make**: Required to run the build scripts. (Mac: `xcode-select --install`, Linux: `sudo apt install make`).
- **GemFire gRPC Protobuf Definition**: Download the VMware Tanzu GemFire gRPC Extension `.tgz` artifact. Extract the `gemfire.proto` file from the archive and place it in the `../proto/gemfire/v1/` directory relative to this app (e.g., `clients/grpc-client/proto/gemfire/v1/gemfire.proto`).

## Steps

1. From the `gemfire-examples/clients/grpc-client/interop` directory, build both halves: the Go
   gRPC client, and the Java GemFire client plus the listener jar the servers load.

        $ make build

2. Next start a locator, start a server, deploy the listener, and create the region.

        $ gfsh run --file=scripts/start.gfsh

3. Run [`main.go --mode=put`](main.go), the Go gRPC client. It packs each `Person` with
   `anypb.New` and sends it through `CacheService.Put`, leaving two entries in the region that
   were written entirely over gRPC.

        $ GRPC_XDS_BOOTSTRAP=scripts/xds-bootstrap.json ./interop-client --mode=put

4. Observe that the server logs the two entries through the 
   [`PersonCacheListener`](src/main/java/com/vmware/gemfire/examples/grpc/interop/PersonCacheListener.java). 
   It fired as those puts landed, reading `first_name` and `last_name` by name through GemFire's `Document`
   while naming no Protobuf type. This step only greps the line it already wrote.

        $ grep PersonCacheListener server1/server1.log

   Expected output, one line per entry:

        [info ... server1 <grpc-worker-1> tid=0x54] [PersonCacheListener] afterCreate key=alice originRemote=false valueClass=com.vmware.gemfire.proto.internal.document.ProtobufMessageCached first_name=Alice last_name=Anderson

   The `grpc-worker` thread name is the point of the example: a gRPC call drove a GemFire
   cache listener.

5. Run
   [`GemFireClientExample`](src/main/java/com/vmware/gemfire/examples/grpc/interop/GemFireClientExample.java),
   the Java GemFire client. It reads both gRPC-written entries as typed `Person` objects with
   `unpack`, then writes `grace` of its own with `ProtobufMessage.pack`.

        $ make run-gemfire-client

6. Run the Go gRPC client again, this time with [`main.go --mode=get`](main.go). It reads `grace` back
   as a `google.protobuf.Any` and unpacks it, closing the loop in the other direction.

        $ GRPC_XDS_BOOTSTRAP=scripts/xds-bootstrap.json ./interop-client --mode=get

7. Shut down the system.

        $ gfsh run --file=scripts/stop.gfsh

8. Clean up. This removes the build output, the generated Go stubs, the `interop-client` binary,
   and the `locator` and `server1` directories that `start.gfsh` leaves behind in this directory.

        $ make clean
