#!/usr/bin/env bash

if [ -z ${GEMFIRE_HOME} ]; then
    echo "GEMFIRE_HOME must be set to a valid path."
    exit 1
else
    echo "GEMFIRE_HOME: ${GEMFIRE_HOME}"
    echo "GFSH Version: $( ${GEMFIRE_HOME}/bin/gfsh version ) "
fi

if [ -z ${JAVA_HOME} ]; then
    echo "JAVA_HOME must be set to a valid path."
    exit 1
else
    echo
    echo "JAVA_HOME: ${JAVA_HOME}"
    echo "Java Version:"
    ${JAVA_HOME}/bin/java -version
    echo
fi

export PATH=${JAVA_HOME}/bin:${GEMFIRE_HOME}/bin:${PATH}:.

KEYSTORES_DIR="$(cd "$(dirname "./keys")"; pwd)/$(basename "./keys")"
COMMON_CONNECT=" --use-ssl=true --key-store=${KEYSTORES_DIR}/keystore.jks --trust-store=${KEYSTORES_DIR}/truststore.jks --trust-store-password=password --key-store-password=password "


gfsh -e "connect --locator=localhost[10334] ${COMMON_CONNECT} " -e "shutdown --include-locators=true"
