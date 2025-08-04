#!/usr/bin/env bash

#
# Copyright 2024 Broadcom. All rights reserved.
#

# Example script to start a server with custom application classes.
#
# Usage:
# start-server.sh [option ...]
#
# For help on options run:
# start-server.sh --help

set -e -o pipefail

# Absolute path to Java installation to use, if JAVA_HOME is not already set.
JAVA_HOME=${JAVA_HOME:-"/path/to/java"}

# Absolute path the installation location of GemFire.
# MUST export, launcher needs to know where GemFire is installed.
export GEMFIRE_HOME="/path/to/gemfire"

# Absolute path to your custom application.
# DO NOT export, only this script needs to know where your application is installed.
APPLICATION_HOME="/path/to/application"

# Include each jar necessary for your application, including third party dependencies.
# USE CAUTION when using wildcard or other globbing patterns as it may include undesired jars.
# DO NOT include GemFire jars or any other jars from the GEMFIRE_HOME/lib directory.
# DO NOT export, launcher does not use this environment variable.
AUTOMATIC_MODULE_CLASSPATH="${APPLICATION_HOME}/lib/my-application.jar:${APPLICATION_HOME}/lib/my-domain-classes.jar"

# Name of this server.
# DO NOT export, launcher does not use this environment variable.
SERVER_NAME="server-1"

# Optional hard coded server options. Use the --help option on this script for details.
# DO NOT export, launcher does not use this environment variable.
SERVER_OPTIONS=("--server-bind-address=1.2.3.4" "--server-port=1234")

# Optional hard coded Java options. Use the -help option on java for details.
# Set any GemFire system properties here.
# DO NOT export, launcher does not use this environment variable.
JAVA_OPTIONS=("-Xmx64g" "-Dgemfire.default.locators=locator-1[10334],locators-2[10334]" "-Dgemfire.example.property=some value")

# Starts a server forwarding any command line options from this script.
# MUST only include gemfire-bootstrap jar on the Java -classpath option.
# MUST double quote $@ for properly forward option values with spaces.
"${JAVA_HOME}"/bin/java "${JAVA_OPTIONS[@]}" \
  -classpath "${GEMFIRE_HOME}"/lib/gemfire-bootstrap-*.jar \
  com.vmware.gemfire.bootstrap.ServerLauncher \
  --automatic-module-classpath="${AUTOMATIC_MODULE_CLASSPATH}" \
  start "${SERVER_NAME}" \
  "${SERVER_OPTIONS[@]}" \
  "$@"
