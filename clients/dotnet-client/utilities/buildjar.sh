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

if [ -f ${GEMFIRE_HOME}/lib/gemfire-dependencies.jar ]; then
    export CLASSPATH=${GEMFIRE_HOME}/lib/gemfire-dependencies.jar:.
    echo "Using gemfire-dependencies.jar"
else
    export CLASSPATH=${GEMFIRE_HOME}/lib/geode-dependencies.jar:.
    echo "Using geode-dependencies.jar"
fi

echo "**** Build Classes ****"
javac ./example/*.java ./javaobject/*.java
echo "**** Create example.jar ****"
jar cvf example.jar */*.class
echo
export CLASSPATH=
