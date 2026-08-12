#!/usr/bin/env bash
#
# Runs the Python example client end to end against a local GemFire locator + server cluster
# over xds:///gemfire_grpc. Automates the manual steps in this directory's README.
#
# Usage: examples/python-client/run-python-client.sh [--tls]
#   --tls   Enable TLS on the gRPC endpoints and use a secure xDS channel (default: plaintext)
#
# Requires GEMFIRE_HOME to be set to a local GemFire installation and Python 3
# with pip available.

set -euo pipefail

export CLIENT_LANG="python"

source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/gemfire-grpc-setup.sh"

parseFlags "$@"
trap stopCluster EXIT

startCluster

echo
echo "=== Running the Python example client ==="
(cd "$(dirname "${BASH_SOURCE[0]}")" && make build)
export GRPC_XDS_BOOTSTRAP="$bootstrapFile"
export GRPC_DEFAULT_SSL_ROOTS_FILE_PATH="${SSL_CERT_FILE:-}"

"$(dirname "${BASH_SOURCE[0]}")/.venv/bin/python" "$(dirname "${BASH_SOURCE[0]}")/main.py"

echo
echo "Done."
