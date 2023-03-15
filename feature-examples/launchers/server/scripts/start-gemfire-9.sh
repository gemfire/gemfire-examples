#!/usr/bin/env bash

#Copyright (c) VMware, Inc. 2023. All rights reserved.
#SPDX-License-Identifier: Apache-2.0

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

if [ -z "${GEODE_HOME}" ]; then
  echo "Environment GEODE_HOME not set."
  exit 1
fi

# Find the GemFire dDependencies Jar file
GEMFIRE_DEPENDENCIES="${GEODE_HOME}"/lib/geode-dependencies.jar
if [ ! -e "${GEMFIRE_DEPENDENCIES}" ]; then
  echo "Environment GEODE_HOME does not reference a GemFire installation."
  exit 1
fi

# Run application directly with GemFire Dependencies on the classpath
java -classpath "${GEMFIRE_DEPENDENCIES}":"${SCRIPT_DIR}"/../build/classes/java/main \
  com.vmware.gemfire.examples.launchers.ExampleServerApplication \
  "Hello world!"
