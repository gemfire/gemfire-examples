<!--
  ~ Copyright 2023-2024 Broadcom. All rights reserved.
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

# Tanzu GemFire feature examples
The examples in this folder are showcase features of GemFire and demonstrate their basic usage.

For details on all GemFire features, see [Tanzu GemFire Documentation](https://docs.vmware.com/en/VMware-GemFire).

## Tanzu GemFire Version
Your client code must link against the _same or older_ version (ignoring patch versions) of Tanzu GemFire as the Tanzu GemFire server it will connect to.

To link against an older version of GemFire, edit the `gemfireVersion=` line in `gradle.properties`.

## Prerequisites

In order to execute the examples in this project, follow these steps:
1. Ensure [JDK 11](https://bell-sw.com/pages/downloads/) or JDK 17 is installed (and set JAVA_HOME if it's not the default).  JDK 8 *can* be used, but some examples such as lucene and luceneSpatial will not work.
1. Download the version of GemFire that you want to use as the server from [Broadcom Customer Support](https://support.broadcom.com/)
1. Unpack the GemFire TGZ file (e.g. `tar xzf vmware-gemfire-10.1.0.tgz`)
1. `export GEMFIRE_HOME` to point to the top level directory inside the extracted GemFire. For example, if you extracted in /tmp/downloads, `export GEMFIRE_HOME=/tmp/downloads/vmware-gemfire-10.1.0`.
1. When you first run the gradle commands below you'll be prompted to supply credentials.  Set gemfireReleaseRepoUser but leave gemfireReleaseRepoPassword blank for instructions how to obtain an access token.

## Running an example

You can now run an example with the following gradle targets:

* `build` - compiles the example and runs unit tests
* `start` - initializes the Tanzu GemFire cluster
* `run` - runs the example Application
* `stop` - shuts down the cluster
* `runAll` - invokes start, run, stop

The commands you need to run a specific example will be given in the `README.md` file. Sample
usage:

    $ ./gradlew :replicated:start
    $ ./gradlew :replicated:run
    $ ./gradlew :replicated:stop

## Catalog of examples

The following sections call out ready-made examples.  You may want to start your journey with the [Tanzu GemFire Documentation](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-about_gemfire.html).

### Basics

*  [Replicated Region](replicated/)
*  [Partitioned Region](partitioned/)
*  [Put Multiple Values at Once](putall/)
*  [Functions](functions/)
*  [Persistence](persistence/)
*  [OQL (Querying)](queries/)

### Intermediate

*  [Serialization](serialization/)
*  [Lucene Indexing](lucene/)
*  [OQL Indexing](indexes/)
*  [Cache Loader](loader/)
*  [Cache Writer](writer/)
*  [Cache Listeners](listener/)
*  [Async Event Queues & Async Event Listeners](async/)
*  [Continuous Querying](cq/)
*  [Transaction](transaction/)
*  [Eviction](eviction/)
*  [Expiration](expiration/)
*  [Overflow](overflow/)
*  [Security & SSL](clientSecurity/)
*  [Colocation](colocation/)
*  [Rest](rest/)
*  [JSON](json/)

### Advanced

*  [Lucene Spatial Indexing](luceneSpatial/)
*  [WAN Gateway](wan/)
*  [WAN Gateway Delta](wanDelta/)
*  [Durable Messaging for Subscriptions](durableMessaging/)
*  [Micrometer Metrics](micrometerMetrics/)
*  [Compression](compression/)
*  [Launchers](launchers/)
