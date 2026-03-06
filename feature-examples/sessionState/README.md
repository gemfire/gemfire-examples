<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->
<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# GemFire Session State Example using Tomcat

This is a simple example which demonstrates how to setup and use the Session Management Module for Tomcat.

This example assumes you have GemFire 10.2+ and Java 17 installed. It also assumes you have a local copy of Tomcat 11 downloaded.
For more information about how to set up the Tomcat module with your version of Tomcat and GemFire see the official documentation at:
`https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/tools_modules-http_session_mgmt-tomcat_installing_the_module.html`

The Session Management extension (version 1.1.1 or above) is distributed separately as a `.tgz` file. Download it from the
[Broadcom Support Portal](https://support.broadcom.com/) and place it in `$GEMFIRE_HOME/extensions/`.

## Steps

1. Set the environment variable `$CATALINA_HOME` to point at the root directory of your local Tomcat 11 installation. This is a
Tomcat convention so it may already be set.

2. Add the GemFire session listener and manager to Tomcat's configuration files:

   In `$CATALINA_HOME/conf/server.xml`, add inside the `<Server>` element:
  ```xml
<Listener className="org.apache.geode.modules.session.catalina.ClientServerCacheLifecycleListener"/>
  ```

   In `$CATALINA_HOME/conf/context.xml`, add inside the `<Context>` element:
  ```xml
<Manager className="org.apache.geode.modules.session.catalina.Tomcat11DeltaSessionManager"/>
  ```

3. Run the setup script, passing the root directory of your GemFire installation:

  ```
  cd scripts
  ./example-setup.sh <root directory of GemFire install>
  ```

5. Visit the example webapp in your browser:

  ```
  http://localhost:8080/SessionStateDemo/index
  ```

6. You should see details about your current session. Use the input prompts to set, get, and delete session attributes.

7. To shut down the GemFire cluster:

  ```bash
  cd scripts
  ./example-shutdown.sh $GEMFIRE_HOME
  ```
