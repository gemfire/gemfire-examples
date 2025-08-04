#!/usr/bin/env bash

#
# Copyright 2024 Broadcom. All rights reserved.
#

# Example gfsh wrapper to include custom application classes. It adds customer application libraries
# to the proper environment variable for gfsh and gfsh managed processes to find your application
# classes.
#
# Usage:
# run-gfsh.sh [option ...]
#
# For help on options run:
# run-gfsh.sh --help

set -e -o pipefail

# Absolute path the installation location of GemFire.
# DO NOT export, gfsh script does not use this variable.
GEMFIRE_HOME="/path/to/gemfire"

# Absolute path to your custom application.
# DO NOT export, gfsh script does not use this variable.
APPLICATION_HOME="/path/to/application"

# Include each jar necessary for your application, including third party dependencies.
# USE CAUTION when using wildcard or other globbing patterns as it may include undesired jars.
# DO NOT include GemFire jars or any other jars from the GEMFIRE_HOME/lib directory.
# MUST export, gfsh script uses this variable to find your application libraries.
export AUTOMATIC_MODULE_CLASSPATH="${APPLICATION_HOME}/lib/my-application.jar:${APPLICATION_HOME}/lib/my-domain-classes.jar"

# Invokes gfsh forwarding any command line options from this script.
# MUST double quote $@ for properly forward option values with spaces.
"${GEMFIRE_HOME}"/bin/gfsh "$@"
