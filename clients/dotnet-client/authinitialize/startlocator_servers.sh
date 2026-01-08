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

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

UTILITYJARPATH="${SCRIPT_DIR}/../utilities/example.jar"

JARDIR="$(cd "$(dirname "${UTILITYJARPATH}")"; pwd)/$(basename "${UTILITYJARPATH}")"

CLASSPATH=${JARDIR}:.
COMMON_OPTS="--J=-Dgemfire.security-username=server"
COMMON_OPTS="${COMMON_OPTS} --J=-Dgemfire.security-password=server"
COMMON_OPTS="${COMMON_OPTS} --classpath=${JARDIR}"
LOCATOR_OPTS="${COMMON_OPTS} --J=-Dgemfire.security-manager=javaobject.SimpleSecurityManager"

gfsh -e "start locator --J=-Dgemfire.security-manager=javaobject.SimpleSecurityManager --name=locator0 --classpath=${JARDIR}"
gfsh -e "connect --locator=localhost[10334] --user=server --password=server" -e "deploy --jar=${JARDIR}"
gfsh -e "connect --locator=localhost[10334] --user=server --password=server" -e "start server --name=server0 ${COMMON_OPTS}"
gfsh -e "connect --locator=localhost[10334] --user=server --password=server" -e "start server --name=server1 --server-port=40405 --http-service-port=7071 ${COMMON_OPTS}"
gfsh -e "connect --locator=localhost[10334] --user=server --password=server" -e "create region --name=region --type=PARTITION"
