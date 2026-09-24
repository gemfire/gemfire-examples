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
# 2026-03-04: Added pre-setup cleanup of any previously-installed GemFire JARs and copying of
#             GemFire runtime JARs to Tomcat lib/ so the session management module resolves at startup
# 2026-03-04: Switch GemFire client dependency source from lib/ to the curated
#             gemfire-client-dependencies zip in tools/Modules/gemfire-session-management/
#

# Usage: ./example-setup.sh <root directory of GemFire install>
# Example: ./example-setup.sh /path/to/vmware-gemfire
#
# Prerequisites:
#   - CATALINA_HOME must be set to the ROOT of your Apache Tomcat 11 installation
#     (e.g. /opt/apache-tomcat-11.0.18  — NOT the bin/ subdirectory)
#   - GemFire must be installed at the path passed as the first argument
#   - The session management extension tgz must be present in $GEMFIRE_HOME/extensions/

set -eu

GEMFIRE_LOCATION=${1%/}

if [ -z "${GEMFIRE_LOCATION}" ]; then
  echo "Usage: $0 <root directory of GemFire install>"
  exit 1
fi

if [ ! -d "${GEMFIRE_LOCATION}" ]; then
  echo "ERROR: GemFire directory not found: ${GEMFIRE_LOCATION}"
  exit 1
fi

if [ -z "${CATALINA_HOME:-}" ]; then
  echo "ERROR: CATALINA_HOME environment variable is not set."
  echo "       Set it to the ROOT of your Tomcat 11 installation (not the bin/ subdirectory)."
  exit 1
fi

CATALINA_LOCATION=${CATALINA_HOME%/}

# Guard against a common mistake: CATALINA_HOME pointing at bin/ instead of the Tomcat root.
# The root must contain a conf/ directory and a webapps/ directory.
if [ ! -d "${CATALINA_LOCATION}/conf" ] || [ ! -d "${CATALINA_LOCATION}/webapps" ]; then
  echo "ERROR: CATALINA_HOME does not look like a Tomcat root directory."
  echo "       Expected to find conf/ and webapps/ inside: ${CATALINA_LOCATION}"
  echo "       Make sure CATALINA_HOME points at the Tomcat root, not the bin/ subdirectory."
  exit 1
fi

TOMCAT_VERSION=Tomcat11
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

# Locate the curated client-dependencies zip shipped with GemFire at:
#   tools/Modules/gemfire-session-management/gemfire-client-dependencies-*.zip
# This zip contains exactly the GemFire client-side JARs needed by the session
# management module — no server-only or launcher JARs.
CLIENT_DEPS_ZIP=$(ls "${GEMFIRE_LOCATION}/tools/Modules/gemfire-session-management"/gemfire-client-dependencies-*.zip 2>/dev/null | head -1)

if [ -z "${CLIENT_DEPS_ZIP}" ]; then
  echo "ERROR: gemfire-client-dependencies zip not found in"
  echo "       ${GEMFIRE_LOCATION}/tools/Modules/gemfire-session-management/"
  echo "       Ensure GemFire is fully installed."
  exit 1
fi

echo "Using GemFire client dependencies: ${CLIENT_DEPS_ZIP}"

# ---------------------------------------------------------------------------
# Step 1: Clean any GemFire files left from a previous setup run so we always
#         install fresh JARs that match the currently supplied GemFire version.
# ---------------------------------------------------------------------------
echo "Cleaning up any previously installed GemFire files from Tomcat..."

# Remove session management JARs (from previous session-mgmt zip extractions)
rm -f "${CATALINA_LOCATION}/lib/gemfire-session-management-"*.jar

# Remove GemFire client dependency JARs (listed inside the client-deps zip)
while IFS= read -r fname; do
  rm -f "${CATALINA_LOCATION}/lib/${fname}"
done < <(unzip -Z1 "${CLIENT_DEPS_ZIP}")

# Remove GemFire cache configuration files
rm -f "${CATALINA_LOCATION}/conf/cache-client.xml"
rm -f "${CATALINA_LOCATION}/conf/cache-peer.xml"
rm -f "${CATALINA_LOCATION}/conf/cache-server.xml"

# Remove the previous .gfm extension from GemFire's extensions directory
rm -f "${GEMFIRE_LOCATION}/extensions/gemfire-session-management-tomcat11-"*.gfm

# Remove any leftover directories from a misconfigured run where CATALINA_HOME
# was mistakenly pointed at the bin/ subdirectory (creates bin/lib/ and bin/conf/)
rm -rf "${CATALINA_LOCATION}/bin/lib"
rm -rf "${CATALINA_LOCATION}/bin/conf"

echo "Cleanup done."

# ---------------------------------------------------------------------------
# Step 2: Locate the session management tgz in the GemFire extensions directory
# ---------------------------------------------------------------------------
SESSION_MGMT_TGZ=$(ls "${GEMFIRE_LOCATION}/extensions"/gemfire-session-management-*.tgz 2>/dev/null | head -1)

if [ -z "${SESSION_MGMT_TGZ}" ]; then
  echo "ERROR: Session management extension tgz not found in ${GEMFIRE_LOCATION}/extensions/"
  echo "       Download it from the Broadcom Support Portal and place it there."
  exit 1
fi

echo "Using session management extension: ${SESSION_MGMT_TGZ}"

# Extract to a temp directory that is removed on exit
SESSION_MGMT_TMP=$(mktemp -d)
trap "rm -rf ${SESSION_MGMT_TMP}" EXIT
tar xzf "${SESSION_MGMT_TGZ}" -C "${SESSION_MGMT_TMP}"

SESSION_MGMT_DIR=$(ls -d "${SESSION_MGMT_TMP}"/gemfire-session-management-* 2>/dev/null | head -1)
if [ -z "${SESSION_MGMT_DIR}" ]; then
  echo "ERROR: Could not find extracted session management directory in ${SESSION_MGMT_TMP}"
  exit 1
fi

# ---------------------------------------------------------------------------
# Step 3: Install the Tomcat 11 session management JARs into $CATALINA_HOME/lib/
#         These are the two gemfire-session-management-tomcat11-*.jar files that
#         provide the Tomcat Manager and Lifecycle Listener classes.
# ---------------------------------------------------------------------------
TOMCAT_ZIP=$(ls "${SESSION_MGMT_DIR}/${TOMCAT_VERSION}"/*.zip 2>/dev/null | head -1)
if [ -z "${TOMCAT_ZIP}" ]; then
  echo "ERROR: ${TOMCAT_VERSION} module zip not found in ${SESSION_MGMT_DIR}/${TOMCAT_VERSION}/"
  exit 1
fi

echo "Installing ${TOMCAT_VERSION} session management JARs to ${CATALINA_LOCATION}/lib/..."
unzip -o "${TOMCAT_ZIP}" -d "${CATALINA_LOCATION}/lib"

# The zip bundles a conf/ subdirectory with the cache XML files — move them to conf/
if [ -d "${CATALINA_LOCATION}/lib/conf" ]; then
  echo "Copying GemFire cache configuration files to ${CATALINA_LOCATION}/conf/..."
  cp -a "${CATALINA_LOCATION}/lib/conf/." "${CATALINA_LOCATION}/conf/"
  rm -rf "${CATALINA_LOCATION}/lib/conf/"
fi

# ---------------------------------------------------------------------------
# Step 4: Install GemFire client dependency JARs into $CATALINA_HOME/lib/
#         tools/Modules/gemfire-session-management/gemfire-client-dependencies-*.zip
#         contains the curated set of GemFire client-side JARs (gemfire-core,
#         gemfire-management, and their transitive dependencies) that the session
#         management module needs on Tomcat's shared classpath. Using this zip
#         instead of lib/ ensures only the necessary client JARs are installed.
# ---------------------------------------------------------------------------
echo "Installing GemFire client dependency JARs from ${CLIENT_DEPS_ZIP}..."
unzip -o "${CLIENT_DEPS_ZIP}" -d "${CATALINA_LOCATION}/lib"

# ---------------------------------------------------------------------------
# Step 5: Install the .gfm extension into GemFire's extensions directory so
#         the running GemFire server can load the session management module.
# ---------------------------------------------------------------------------
TOMCAT_GFM=$(ls "${SESSION_MGMT_DIR}/${TOMCAT_VERSION}"/*.gfm 2>/dev/null | head -1)
if [ -n "${TOMCAT_GFM}" ]; then
  echo "Copying session management gfm extension to ${GEMFIRE_LOCATION}/extensions/..."
  mkdir -p "${GEMFIRE_LOCATION}/extensions"
  cp "${TOMCAT_GFM}" "${GEMFIRE_LOCATION}/extensions/"
fi

# ---------------------------------------------------------------------------
# Step 6: Start GemFire locator and server
# ---------------------------------------------------------------------------
echo "Starting GemFire locator and server..."
"${GEMFIRE_LOCATION}/bin/gfsh" \
  -e "start locator --name=locator1" \
  -e "start server --name=server1 --server-port=40404 --locators=localhost[10334]"

# ---------------------------------------------------------------------------
# Step 7: Build the sample webapp and deploy it to Tomcat
# ---------------------------------------------------------------------------
PROJECT_ROOT=$(cd "${SCRIPT_DIR}/../../.." && pwd)

echo "Building session state demo webapp..."
pushd "${PROJECT_ROOT}/feature-examples/sessionState/webapp"
  ./gradlew clean build
popd

echo "Deploying SessionStateDemo.war to ${CATALINA_LOCATION}/webapps/..."
rm -rf "${CATALINA_LOCATION}/webapps/SessionStateDemo.war"
rm -rf "${CATALINA_LOCATION}/webapps/SessionStateDemo"
cp "${PROJECT_ROOT}/feature-examples/sessionState/webapp/build/libs/SessionStateDemo-1.0-SNAPSHOT.war" \
   "${CATALINA_LOCATION}/webapps/SessionStateDemo.war"

echo ""
echo "Setup complete!"
echo "Start Tomcat with: \${CATALINA_HOME}/bin/startup.sh"
echo "Then visit: http://localhost:8080/SessionStateDemo/index"
