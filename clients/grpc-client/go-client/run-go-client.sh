#!/usr/bin/env bash
#
# Runs the Go example client end to end against a local GemFire locator + server cluster
# over xds:///gemfire_grpc. Automates the manual steps in this directory's README.
#
# Usage: examples/go-client/run-go-client.sh [--tls]
#   --tls   Enable TLS on the gRPC endpoints and use a secure xDS channel (default: plaintext)
#
# Requires GEMFIRE_HOME to be set to a local GemFire installation and this repo's Go
# toolchain prerequisites (see the Prerequisites section in this directory's README).

set -euo pipefail

source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/gemfire-grpc-setup.sh"

parseFlags "$@"
trap stopCluster EXIT

startCluster

echo
echo "=== Running the Go example client ==="
(cd "$(dirname "${BASH_SOURCE[0]}")" && make build)
GRPC_XDS_BOOTSTRAP="$bootstrapFile" "$(dirname "${BASH_SOURCE[0]}")/cache-client"

echo
echo "Done."
