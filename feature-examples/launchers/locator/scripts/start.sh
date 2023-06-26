#!/usr/bin/env bash

#Copyright (c) VMware, Inc. 2023. All rights reserved.
#SPDX-License-Identifier: Apache-2.0

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

if [ -z "${GEMFIRE_HOME}" ]; then
  echo "Environment GEMFIRE_HOME not set."
  exit 1
fi

# Find the GemFire Bootstrap Jar file
GEMFIRE_BOOTSTRAP=("${GEMFIRE_HOME}"/lib/gemfire-bootstrap-*.jar)
if [ ! -e "${GEMFIRE_BOOTSTRAP[0]}" ]; then
  echo "Environment GEMFIRE_HOME does not reference a GemFire installation."
  exit 1
fi

# Run application via GemFire Bootstrap (com.vmware.gemfire.bootstrap.Main)
java -classpath "${GEMFIRE_BOOTSTRAP[0]}" \
  com.vmware.gemfire.bootstrap.Main \
  com.vmware.gemfire.examples.launchers.ExampleLocatorApplication \
  --automatic-module-classpath "${SCRIPT_DIR}"/../build/classes/java/main \
  "Hello world!"
