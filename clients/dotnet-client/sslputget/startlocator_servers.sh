#!/usr/bin/env bash

if [ -z ${GEMFIRE_HOME} ]; then
    echo "GEMFIRE_HOME must be set to a valid path."
    exit 1
else
    echo
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

UTILITYJARPATH="../utilities/example.jar"

JARDIR="$(cd "$(dirname "${UTILITYJARPATH}")"; pwd)/$(basename "${UTILITYJARPATH}")"
KEYSTORES_DIR="$(cd "$(dirname "./keys")"; pwd)/$(basename "./keys")"

COMMON=" --J=-Dgemfire.ssl-enabled-components=all "
COMMON="${COMMON} --J=-Dgemfire.ssl-keystore=${KEYSTORES_DIR}/keystore.jks "
COMMON="${COMMON} --J=-Dgemfire.ssl-truststore=${KEYSTORES_DIR}/truststore.jks "
COMMON="${COMMON} --J=-Dgemfire.ssl-keystore-password=password "
COMMON="${COMMON} --J=-Dgemfire.ssl-truststore-password=password "

COMMON_CONNECT=" --use-ssl=true --key-store=${KEYSTORES_DIR}/keystore.jks --trust-store=${KEYSTORES_DIR}/truststore.jks --trust-store-password=password --key-store-password=password "

gfsh -e "start locator --name=locator0 --connect=false ${COMMON}"
gfsh -e "connect --locator=localhost[10334] ${COMMON_CONNECT} " -e "deploy --jar=${JARDIR}"
gfsh -e "connect --locator=localhost[10334] ${COMMON_CONNECT} " -e "start server --name=server0 ${COMMON}"
gfsh -e "connect --locator=localhost[10334] ${COMMON_CONNECT}" -e "start server --name=server1 --server-port=40405 --http-service-port=7071 ${COMMON} "
gfsh -e "connect --locator=localhost[10334] ${COMMON_CONNECT} " -e "create region --name=testSSLRegion --type=PARTITION"
