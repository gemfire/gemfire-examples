# go-client

Go gRPC client example for the GemFire gRPC extension.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire installation directory.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
- [Protocol Buffers compiler (`protoc`)](https://protobuf.dev/installation/)
- [OpenSSL](https://openssl-library.org/source/) (if running with TLS enabled)
- **Make**: Required to run the build scripts. (Mac: `xcode-select --install`, Linux: `sudo apt install make`).
- [Go](https://go.dev/doc/install) 1.26 or later
- Go plugins for protoc:
  ```bash
  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
  ```
- **GemFire gRPC Protobuf Definition**: Download the VMware Tanzu GemFire gRPC Extension `.tgz` artifact. Extract the `gemfire.proto` file from the archive and place it in the `../proto/gemfire/v1/` directory relative to this app (e.g., `clients/grpc-client/proto/gemfire/v1/gemfire.proto`).

## Building

Directly with Make:

```bash
make build
```

## Verifying end to end

The fastest way to see this working for real: from the `go-client` directory, run

```bash
./run-go-client.sh
```

You can also run it with TLS enabled by passing `--tls`:
```bash
./run-go-client.sh --tls
```

> **Note for macOS Users (TLS)**: Prior to Go 1.27, Go's `crypto/x509` package on macOS delegates certificate validation to the macOS `Security` framework and ignores the `SSL_CERT_FILE` environment variable. 
> 
> If you are using Go 1.26 or earlier, the `run-go-client.sh --tls` script handles this automatically by temporarily adding the generated CA to your login keychain and removing it when finished (you may be prompted for your Mac password). Go 1.27 and later correctly honors `SSL_CERT_FILE` on macOS.

This starts a local locator + server using `gfsh`, creates
`region1`/`region2`, and runs this app against them over a real xDS bootstrap — no manual setup
needed. See [`run-go-client.sh`](run-go-client.sh) for details.

## Running

The client always dials `xds:///gemfire_grpc`. 

To resolve this path, the gRPC client needs an **xDS bootstrap file** that points to your GemFire locator's ADS (Aggregated Discovery Service) port.

### 1. Create the bootstrap file

Create a file named `grpc-xds-bootstrap.json` (the name and location don't matter, as long as you provide the correct path later). Populate it with the following JSON, replacing `<locator-host>:<locator-ads-port>` with your locator's actual address and gRPC port:

```json
{
  "xds_servers": [
    {
      "server_uri": "<locator-host>:<locator-ads-port>",
      "channel_creds": [ { "type": "insecure" } ],
      "server_features": ["xds_v3"]
    }
  ],
  "node": { "id": "go-client-xds-client", "cluster": "gemfire" }
}
```

*(Note: If you are using TLS, the `channel_creds` type should be `"tls"` and additional certificate configuration is required. See the manual steps below.)*

### 2. Run the client

`grpc-go` reads the `GRPC_XDS_BOOTSTRAP` environment variable at process-init time. Set this variable to the absolute path of the file you just created:

```bash
GRPC_XDS_BOOTSTRAP=/absolute/path/to/grpc-xds-bootstrap.json ./cache-client
```

## Doing it manually

The `run-go-client.sh` script automates the following steps. If you want to set up the cluster and run the client manually, follow these instructions.

1. Download and extract the VMware Tanzu GemFire gRPC Extension `.tgz` artifact.
2. Copy the `gemfire-grpc-extension-*.gfm` file to `$GEMFIRE_HOME/extensions/` (or a directory specified by `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
3. Copy the `gemfire.proto` file from the extracted `.tgz` into the `../proto/gemfire/v1/` directory.
4. Start a locator and a server, binding the gRPC port.

   **For Plaintext (Insecure):**
   ```bash
   $GEMFIRE_HOME/bin/gfsh start locator --name=locator-0 --bind-address=127.0.0.1 --port=10334 \
       --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.use-cluster-configuration=true \
       --J=-Dgemfire.grpc.bind-address=127.0.0.1:41051

   $GEMFIRE_HOME/bin/gfsh start server --name=server-0 --bind-address=127.0.0.1 --locators=127.0.0.1[10334] \
       --J=-Dgemfire.use-cluster-configuration=true \
       --J=-Dgemfire.locator-wait-time=60 \
       --J=-Dgemfire.grpc.bind-address=127.0.0.1:41052
   ```

   **For TLS:**
   ```bash
   $GEMFIRE_HOME/bin/gfsh start locator --name=locator-0 --bind-address=127.0.0.1 --port=10334 \
       --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.use-cluster-configuration=true \
       --J=-Dgemfire.grpc.bind-address=127.0.0.1:41051 \
       --J=-Dgemfire.ssl-enabled-components=grpc --J=-Dgemfire.ssl-keystore=/path/to/keystore.jks --J=-Dgemfire.ssl-keystore-password=password --J=-Dgemfire.ssl-truststore=/path/to/truststore.jks --J=-Dgemfire.ssl-truststore-password=password --J=-Dgemfire.ssl-require-authentication=false

   $GEMFIRE_HOME/bin/gfsh start server --name=server-0 --bind-address=127.0.0.1 --locators=127.0.0.1[10334] \
       --J=-Dgemfire.use-cluster-configuration=true \
       --J=-Dgemfire.locator-wait-time=60 \
       --J=-Dgemfire.grpc.bind-address=127.0.0.1:41052 \
       --J=-Dgemfire.ssl-enabled-components=grpc --J=-Dgemfire.ssl-keystore=/path/to/keystore.jks --J=-Dgemfire.ssl-keystore-password=password --J=-Dgemfire.ssl-truststore=/path/to/truststore.jks --J=-Dgemfire.ssl-truststore-password=password --J=-Dgemfire.ssl-require-authentication=false
   ```
5. Create the regions this demo uses:
   ```bash
   $GEMFIRE_HOME/bin/gfsh -e "connect --locator=127.0.0.1[10334]" \
     -e "create region --name=region1 --type=REPLICATE" \
     -e "create region --name=region2 --type=REPLICATE"
   ```
6. Write a bootstrap file pointing at the locator's ADS port (see the [JSON example](#1-create-the-bootstrap-file)
   above, with `server_uri` set to `127.0.0.1:41051` and `channel_creds` type set to `"tls"` if using TLS).
7. If using TLS, ensure Go trusts the CA certificate. On Linux, export the path:
   ```bash
   export SSL_CERT_FILE=/path/to/ca.pem
   ```
   *On macOS, if you are using Go 1.27 or later, `SSL_CERT_FILE` works exactly like Linux. If you are using Go 1.26 or earlier, because Go ignores `SSL_CERT_FILE`, you must add it to your keychain instead:*
   ```bash
   security add-trusted-cert -d -r trustRoot -k ~/Library/Keychains/login.keychain-db /path/to/ca.pem
   ```
   *(To clean up afterward: `security delete-certificate -Z $(openssl x509 -in /path/to/ca.pem -noout -fingerprint -sha1 | cut -d= -f2 | tr -d :) -t ~/Library/Keychains/login.keychain-db`)*
8. Run this app with `GRPC_XDS_BOOTSTRAP=/path/to/that/file` as shown under [Running](#running).
9. Clean up:
   ```bash
   $GEMFIRE_HOME/bin/gfsh -e "stop server --name=server-0" -e "stop locator --name=locator-0"
   ```

## What it does

Exercises Put, Get, Remove, GetAndPut, and GetAndRemove operations with string, int, and Any (Person) keys, and tests error-handling for non-existent keys and regions.

## Cleaning up

```bash
make clean
```


