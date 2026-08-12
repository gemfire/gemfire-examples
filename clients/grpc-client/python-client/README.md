# python-client

Python gRPC client example for the GemFire gRPC extension.

## Prerequisites

- **GemFire Installation**: `GEMFIRE_HOME` must be set to a valid GemFire installation directory.
- **gRPC Extension**: The VMware Tanzu GemFire gRPC Extension (`.gfm` file) must be installed in `$GEMFIRE_HOME/extensions/` (or specified via `$GEMFIRE_EXTENSIONS_REPOSITORY_PATH`).
- [Protocol Buffers compiler (`protoc`)](https://protobuf.dev/installation/)
- [OpenSSL](https://openssl-library.org/source/) (if running with TLS enabled)
- **Make**: Required to run the build scripts. (Mac: `xcode-select --install`, Linux: `sudo apt install make`).
- [Python](https://www.python.org/downloads/) 3.8 or later
- **Python Tools**: `pip` and `venv` must be available. These are usually bundled with Python, but on Linux you may need to install them (e.g., `sudo apt install python3-pip python3-venv`).
- **GemFire gRPC Protobuf Definition**: Download the VMware Tanzu GemFire gRPC Extension `.tgz` artifact. Extract the `gemfire.proto` file from the archive and place it in the `../proto/gemfire/v1/` directory relative to this app (e.g., `clients/grpc-client/proto/gemfire/v1/gemfire.proto`).

## Building

Directly with Make:

```bash
make build
```

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
  "node": { "id": "python-client-xds-client", "cluster": "gemfire" }
}
```

*(Note: If you are using TLS, the `channel_creds` type should be `"tls"` and additional certificate configuration is required. See the manual steps below.)*

### 2. Run the client

`grpcio` reads the `GRPC_XDS_BOOTSTRAP` environment variable at process-init time. Set this variable to the absolute path of the file you just created:

```bash
GRPC_XDS_BOOTSTRAP=/absolute/path/to/grpc-xds-bootstrap.json make run
```

## Verifying end to end

The fastest way to see this working for real: from the `python-client` directory, run

```bash
./run-python-client.sh
```

You can also run it with TLS enabled by passing `--tls`:
```bash
./run-python-client.sh --tls
```

This starts a local locator + server using `gfsh`, creates
`region1`/`region2`, and runs this app against them over a real xDS bootstrap — no manual setup
needed. See [`run-python-client.sh`](run-python-client.sh) for details.

## Doing it manually

The `run-python-client.sh` script automates the following steps. If you want to set up the cluster and run the client manually, follow these instructions.

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
7. If using TLS, ensure Python trusts the CA certificate. Export the path to the root certificates:
   ```bash
   export GRPC_DEFAULT_SSL_ROOTS_FILE_PATH=/path/to/ca.pem
   ```
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
