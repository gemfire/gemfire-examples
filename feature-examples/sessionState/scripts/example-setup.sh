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

#!/bin/bash

# enable tracing
set -eux

GEMFIRE_DOWNLOAD_TAR_LOCATION=${1%/}
# Set CATALINA_HOME to the root folder of Apache Tomcat9 installation
CATALINA_LOCATION=${CATALINA_HOME%/}
TOMCAT_VERSION=Tomcat9

BASE_DIR=$(pwd)
mkdir "${BASE_DIR}"/vmware-gemfire
tar zxvf "${GEMFIRE_DOWNLOAD_TAR_LOCATION}"/*.tgz --strip-components=1 -C "${BASE_DIR}"/vmware-gemfire
ls -l "${BASE_DIR}"/vmware-gemfire
pushd vmware-gemfire
  mkdir extensions
  cp tools/Modules/${TOMCAT_VERSION}/*.gfm extensions
  pwd
  unzip -o tools/Modules/${TOMCAT_VERSION}/*.zip -d ${CATALINA_LOCATION}/lib
  cp -a ${CATALINA_LOCATION}/lib/conf/. ${CATALINA_LOCATION}/conf/
  rm -rf ${CATALINA_LOCATION}/lib/conf/
  pwd
popd

export GEMFIRE_LOCATION=${BASE_DIR}/vmware-gemfire
${GEMFIRE_LOCATION}/bin/gfsh -e "start locator --name=locator1" -e "start server --name=server1 --server-port=40404 --locators=localhost[10334]"

pwd
#Build sample webapp
pushd feature-examples/sessionState/webapp
./gradlew clean build
popd

pushd feature-examples/sessionState/scripts
#Place freshly built version of webapp into Tomcats webapps directory
rm -rf ${CATALINA_LOCATION}/webapps/SessionStateDemo*
cp ${BASE_DIR}/feature-examples/sessionState/webapp/build/libs/SessionStateDemo-1.0-SNAPSHOT.war ${CATALINA_LOCATION}/webapps/SessionStateDemo.war
popd