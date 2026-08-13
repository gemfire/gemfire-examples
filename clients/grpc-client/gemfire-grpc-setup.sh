#!/usr/bin/env bash
#
# Shared local-cluster setup for the example grpc clients' end-to-end demo scripts. Not meant to be
# run directly — source it from examples/<client>/run-<client>.sh.
#
# Provides: parseFlags, startCluster, stopCluster, and the bootstrapFile
# variable that startCluster populates for the caller to use.
#
# Requires GEMFIRE_HOME to be set to a local GemFire installation.

bootstrapFile="$(mktemp -t grpc-xds-bootstrap)"
useTls="false"
locatorHostPort=""
serverHostPort=""
locatorDir="$(mktemp -d -t grpc-example-locator-XXXXXX)"
serverDir="$(mktemp -d -t grpc-example-server-XXXXXX)"

parseFlags() {
  for argument in "$@"; do
    case "$argument" in
      --tls)
        useTls="true"
        ;;
      *)
        echo "Unknown argument: $argument" >&2
        exit 1
        ;;
    esac
  done
}

pickFreeHostPort() {
  python3 -c "import socket; s = socket.socket(); s.bind(('127.0.0.1', 0)); print(s.getsockname()[1]); s.close()"
}

startCluster() {
  if [[ -z "${GEMFIRE_HOME:-}" ]]; then
    echo "GEMFIRE_HOME is not set. Please set it to your GemFire installation directory." >&2
    exit 1
  fi

  gfshPath="$GEMFIRE_HOME/bin/gfsh"
  if [[ ! -x "$gfshPath" ]]; then
    echo "gfsh not found at $gfshPath. Ensure GEMFIRE_HOME is correct." >&2
    exit 1
  fi

  # Ensure the gRPC extension is installed
  if ! ls "$GEMFIRE_HOME/extensions/"*grpc*.gfm 1> /dev/null 2>&1 && \
     ! ls "${GEMFIRE_EXTENSIONS_REPOSITORY_PATH:-/dev/null}/"*grpc*.gfm 1> /dev/null 2>&1; then
    echo "ERROR: No gRPC extension (.gfm) found in \$GEMFIRE_HOME/extensions/ or \$GEMFIRE_EXTENSIONS_REPOSITORY_PATH." >&2
    echo "Please ensure the VMware Tanzu GemFire gRPC Extension is installed before running this example." >&2
    exit 1
  fi

  locatorHostPort="$(pickFreeHostPort)"
  locatorJmxPort="$(pickFreeHostPort)"
  locatorPeerPort="$(pickFreeHostPort)"
  
  tlsArguments=""
  xdsCredsType="insecure"
  if [[ "$useTls" == "true" ]]; then
    echo "Generating TLS certificates for the cluster..."
    keyStoreFile="$locatorDir/keystore.jks"
    trustStoreFile="$locatorDir/truststore.jks"
    pemFile="$locatorDir/ca.pem"
    password="password"
    
      # Generate self-signed cert for 127.0.0.1 natively with OpenSSL (avoids PKCS12 legacy algorithm issues)
      openssl req -x509 -newkey rsa:2048 -keyout "$locatorDir/private_key.pem" -out "$pemFile" -days 365 -nodes \
        -subj "/CN=localhost/OU=Test/O=Test/L=Test/ST=Test/C=US" \
        -addext "subjectAltName=IP:127.0.0.1,DNS:localhost" >/dev/null 2>&1
      
      # Package into JKS for GemFire
      openssl pkcs12 -export -in "$pemFile" -inkey "$locatorDir/private_key.pem" -out "$locatorDir/keystore.p12" -name gemfire -password pass:"$password" >/dev/null 2>&1
      keytool -importkeystore -srckeystore "$locatorDir/keystore.p12" -srcstoretype PKCS12 -srcstorepass "$password" -destkeystore "$keyStoreFile" -deststoretype JKS -deststorepass "$password" -noprompt >/dev/null 2>&1
      keytool -importcert -alias gemfire-ca -file "$pemFile" -keystore "$trustStoreFile" -storepass "$password" -noprompt >/dev/null 2>&1

    tlsArguments="--J=-Dgemfire.ssl-enabled-components=grpc --J=-Dgemfire.ssl-keystore=$keyStoreFile --J=-Dgemfire.ssl-keystore-password=$password --J=-Dgemfire.ssl-truststore=$trustStoreFile --J=-Dgemfire.ssl-truststore-password=$password --J=-Dgemfire.ssl-require-authentication=false"
    xdsCredsType="tls"

    # Export trust material for the client
    export SSL_CERT_FILE="$pemFile"

    if [[ "${CLIENT_LANG:-}" == "go" ]]; then
      # Go < 1.27 on macOS ignores SSL_CERT_FILE and uses the system keychain instead.
      # Check if we are on macOS and using an older Go version.
      if [[ "$(uname)" == "Darwin" ]]; then
        go_version=$(go version | awk '{print $3}' | sed 's/go//')
        if [[ $(echo -e "$go_version\n1.27" | sort -V | head -n1) == "1.27" || "$go_version" == "1.27" ]]; then
          # Go 1.27 or newer, it will respect SSL_CERT_FILE natively.
          true
        else
          echo "macOS and Go < 1.27 detected: Temporarily adding generated CA to your login keychain so Go can trust it."
          echo "(You may be prompted for your macOS password/Touch ID)"
          security add-trusted-cert -d -r trustRoot -k ~/Library/Keychains/login.keychain-db "$pemFile"
          addedToKeychain="true"
        fi
      fi
    fi
  fi

  echo "Starting locator (ADS port bound at 127.0.0.1:$locatorHostPort)..."
  "$gfshPath" -e "start locator --name=locator-0 --bind-address=127.0.0.1 --port=$locatorPeerPort --http-service-port=0 --dir=$locatorDir --J=-Dgemfire.jmx-manager-port=$locatorJmxPort --J=-Dgemfire.grpc.bind-address=127.0.0.1:$locatorHostPort $tlsArguments" >/dev/null

  serverHostPort="$(pickFreeHostPort)"
  serverPeerPort="$(pickFreeHostPort)"
  
  echo "Starting server (CRUD port bound at 127.0.0.1:$serverHostPort)..."
  "$gfshPath" -e "start server --name=server-0 --bind-address=127.0.0.1 --server-bind-address=127.0.0.1 --locators=127.0.0.1[$locatorPeerPort] --server-port=$serverPeerPort --http-service-port=0 --dir=$serverDir --J=-Dgemfire.grpc.bind-address=127.0.0.1:$serverHostPort $tlsArguments" >/dev/null

  echo "Creating region1/region2..."
  "$gfshPath" \
    -e "connect --locator=127.0.0.1[$locatorPeerPort]" \
    -e "create region --name=region1 --type=REPLICATE --if-not-exists" \
    -e "create region --name=region2 --type=REPLICATE --if-not-exists" >/dev/null

  if [[ "$useTls" == "true" ]]; then
    cat > "$bootstrapFile" <<EOF
{
  "xds_servers": [
    { "server_uri": "127.0.0.1:$locatorHostPort", "channel_creds": [ { "type": "$xdsCredsType" } ], "server_features": ["xds_v3"] }
  ],
  "certificate_providers": {
    "system_root_certs": {
      "plugin_name": "file_watcher",
      "config": {
        "certificate_file": "$pemFile",
        "private_key_file": "$locatorDir/private_key.pem",
        "ca_certificate_file": "$pemFile"
      }
    }
  },
  "node": { "id": "run-e2e-demo", "cluster": "gemfire" }
}
EOF
  else
    cat > "$bootstrapFile" <<EOF
{
  "xds_servers": [
    { "server_uri": "127.0.0.1:$locatorHostPort", "channel_creds": [ { "type": "$xdsCredsType" } ], "server_features": ["xds_v3"] }
  ],
  "node": { "id": "run-e2e-demo", "cluster": "gemfire" }
}
EOF
  fi
}

stopCluster() {
  echo
  echo "Cleaning up GemFire processes..."
  if [[ -n "${GEMFIRE_HOME:-}" ]]; then
    "$GEMFIRE_HOME/bin/gfsh" -e "stop server --dir=$serverDir" >/dev/null 2>&1 || true
    "$GEMFIRE_HOME/bin/gfsh" -e "stop locator --dir=$locatorDir" >/dev/null 2>&1 || true
  fi

  rm -rf "$locatorDir" "$serverDir" >/dev/null 2>&1 || true
  if [[ "${addedToKeychain:-false}" == "true" && -f "$locatorDir/ca.pem" ]]; then
    echo "Removing generated CA from your login keychain..."
    security delete-certificate -Z $(openssl x509 -in "$locatorDir/ca.pem" -noout -fingerprint -sha1 | cut -d= -f2 | tr -d :) -t ~/Library/Keychains/login.keychain-db >/dev/null 2>&1 || true
  fi
  rm -f "$bootstrapFile"
}
