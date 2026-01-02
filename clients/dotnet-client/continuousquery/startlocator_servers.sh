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

gfsh -e "start locator --name=locator0"
gfsh -e "connect --locator=localhost[10334]" -e "deploy --jar=${JARDIR}"
gfsh -e "connect --locator=localhost[10334]" -e "start server --name=server0"
gfsh -e "connect --locator=localhost[10334]" -e "start server --name=server1 --server-port=40405 --http-service-port=7071"
gfsh -e "connect --locator=localhost[10334]" -e "create region --name=example_orderobject --type=PARTITION"
