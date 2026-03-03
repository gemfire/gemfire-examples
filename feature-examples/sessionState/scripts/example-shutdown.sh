#!/bin/bash
#
# Copyright (c) VMware, Inc. 2023. All rights reserved.
#

 #
 # Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 # agreements. See the NOTICE file distributed with this work for additional information regarding
 # copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 # "License"); you may not use this file except in compliance with the License. You may obtain a
 # copy of the License at
 #
 # http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software distributed under the License
 # is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 # or implied. See the License for the specific language governing permissions and limitations under
 # the License.
 #

#
# @AI-Generated
# Generated in whole or in part by Cursor
# Description:
# 2026-03-04: Extended shutdown to stop Tomcat and remove all GemFire-placed files from the
#             Tomcat installation, leaving it in a clean state for the next setup run
#

# Usage: ./example-shutdown.sh <root directory of GemFire install>
# Example: ./example-shutdown.sh /path/to/vmware-gemfire
#
# Prerequisites:
#   - CATALINA_HOME must be set to the root of your Apache Tomcat 11 installation

set -eu

GEMFIRE_LOCATION=${1%/}

if [ -z "${GEMFIRE_LOCATION}" ]; then
  GEMFIRE_LOCATION=${GEMFIRE_HOME:-}
fi

if [ -z "${GEMFIRE_LOCATION}" ]; then
  echo "Usage: $0 <root directory of GemFire install>"
  echo "  or set GEMFIRE_HOME environment variable"
  exit 1
fi

if [ -z "${CATALINA_HOME:-}" ]; then
  echo "ERROR: CATALINA_HOME environment variable is not set."
  exit 1
fi

CATALINA_LOCATION=${CATALINA_HOME%/}
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

# ---------------------------------------------------------------------------
# 1. Stop the GemFire cluster
# ---------------------------------------------------------------------------
echo "Stopping GemFire locator and server..."
"${GEMFIRE_LOCATION}/bin/gfsh" run --file="${SCRIPT_DIR}/stop.gfsh"

# ---------------------------------------------------------------------------
# 2. Stop Tomcat
# ---------------------------------------------------------------------------
echo "Stopping Tomcat..."
if [ -x "${CATALINA_LOCATION}/bin/shutdown.sh" ]; then
  "${CATALINA_LOCATION}/bin/shutdown.sh" || true
else
  chmod +x "${CATALINA_LOCATION}/bin/shutdown.sh"
  "${CATALINA_LOCATION}/bin/shutdown.sh" || true
fi
sleep 3

# ---------------------------------------------------------------------------
# 3. Remove the SessionStateDemo webapp from Tomcat
# ---------------------------------------------------------------------------
echo "Removing SessionStateDemo webapp..."
rm -rf "${CATALINA_LOCATION}/webapps/SessionStateDemo.war"
rm -rf "${CATALINA_LOCATION}/webapps/SessionStateDemo"

# ---------------------------------------------------------------------------
# 4. Remove GemFire JARs from Tomcat's lib/ directory.
#    This covers:
#      a) All JARs that were copied from $GEMFIRE_HOME/lib/
#      b) The session management JARs (gemfire-session-management-*) that
#         were extracted from the session management zip
# ---------------------------------------------------------------------------
echo "Removing GemFire JARs from ${CATALINA_LOCATION}/lib/..."
if [ -d "${CATALINA_LOCATION}/lib" ]; then
  # Remove JARs that exist in GemFire's lib directory (these are GemFire's dependencies)
  for jar in "${GEMFIRE_LOCATION}/lib"/*.jar; do
    fname=$(basename "${jar}")
    rm -f "${CATALINA_LOCATION}/lib/${fname}"
  done
  # Remove the session management module JARs (come from the session management zip, not GemFire lib)
  rm -f "${CATALINA_LOCATION}/lib/gemfire-session-management-"*.jar
fi

# ---------------------------------------------------------------------------
# 5. Remove GemFire cache configuration files from Tomcat's conf/ directory
# ---------------------------------------------------------------------------
echo "Removing GemFire cache config files from ${CATALINA_LOCATION}/conf/..."
rm -f "${CATALINA_LOCATION}/conf/cache-client.xml"
rm -f "${CATALINA_LOCATION}/conf/cache-peer.xml"
rm -f "${CATALINA_LOCATION}/conf/cache-server.xml"

# ---------------------------------------------------------------------------
# 6. Remove the .gfm extension file from GemFire's extensions directory
# ---------------------------------------------------------------------------
echo "Removing session management gfm extension from ${GEMFIRE_LOCATION}/extensions/..."
rm -f "${GEMFIRE_LOCATION}/extensions/gemfire-session-management-tomcat11-"*.gfm

echo ""
echo "Shutdown complete. Tomcat is now in a clean state."
echo "Run example-setup.sh again to reinstall with a fresh GemFire version."
