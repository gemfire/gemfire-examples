<!--
  ~ Copyright (c) VMware, Inc. 2022. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
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
# VMware GemFire examples
The examples in this project are showcase features of GemFire and demonstrate their basic usage. For details an all GemFire featues, see [our documentation](https://docs.vmware.com/en/VMware-Tanzu-GemFire/index.html) 

## VMware GemFire Version
VMware GemFire client code must link against the _same or older_ version (ignoring patch versions) of VMware GemFire as the VMware GemFire server it will connect to.

For compiling against older versions of GemFire you can add the version like this `-PgemfireVersion=10.0.+` to your `./gradlew` command to specify which VMware GemFire client libraries to link.

## Running an example
In order to execute the examples in this project, follow these steps:
1. Download the version of GemFire that you want to use as the server from [Tanzu Network](https://network.tanzu.vmware.com/products/pivotal-gemfire/)
2. Unpack the GemFire TAR file and set your `GEMFIRE_HOME` to point at it. `GEMFIRE_HOME` will be the top level directory inside the extracted GemFire. If you extracted in /tmp/downloads, GEMFIRE_HOME=/tmp/downloads/vmware-gemfire-10.0.0
3. Sign up for the GemFire Maven repo and follow the instructions there to set your authentication credentials for the repo in Gradle
4. You can now run an example with the following gradle targets:

* `build` - compiles the example and runs unit tests
* `start` - initializes the VMware GemFire cluster
* `run` - runs the example Application
* `stop` - shuts down the cluster
* `runAll` - invokes start, run, stop

The commands you need to run a specific example will be given in the `README.md` file. Sample
usage:

    $ ./gradlew :replicated:start
    $ ./gradlew :replicated:run
    $ ./gradlew :replicated:stop
    $ ./gradlew runAll

## Catalog of examples

The following sections call out ready-made examples.  You may want to start your journey with the [VMware GemFire Documentation](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-about_gemfire.html).

### Basics

*  [Replicated Region](replicated/README.md)
*  [Partitioned Region](partitioned/README.md)
*  [Put Multiple Values at Once](putall/README.md)
*  [Functions](functions/README.md)
*  [Persistence](persistence/README.md)
*  [OQL (Querying)](queries/README.md)

### Intermediate

*  [Serialization](serialization/README.md)
*  [Lucene Indexing](lucene/README.md)
*  [OQL Indexing](indexes/README.md)
*  [Cache Loader](loader/README.md)
*  [Cache Writer](writer/README.md)
*  [Cache Listeners](listener/README.md)
*  [Async Event Queues & Async Event Listeners](async/README.md)
*  [Continuous Querying](cq/README.md)
*  [Transaction](transaction/README.md)
*  [Eviction](eviction/README.md)
*  [Expiration](expiration/README.md)
*  [Overflow](overflow/README.md)
*  [Security & SSL](clientSecurity/README.md)
*  [Colocation](colocation/README.md)
*  [Rest](rest/README.md)
*  [JSON](json/README.md)

### Advanced

*  [Lucene Spatial Indexing](luceneSpatial/README.md)
*  [WAN Gateway](wan/README.md)
*  [WAN Gateway Delta](wanDelta/README.md)
*  [Durable Messaging for Subscriptions](durableMessaging/README.md)
*  [Micrometer Metrics](micrometerMetrics/README.md)
*  [Compression](compression/README.md)
