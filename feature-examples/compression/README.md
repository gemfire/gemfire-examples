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

# GemFire Compression example

This is a simple example that demonstrates enabling Compression in a
replicated region. The Default compression algorithm included with GemFire is Snappy. Additionally, you can specify your own compressor algorithm as well by implementing `org.apache.geode.compression.Compression` Interface. For enabling compression on a GemFire region you can follow the official [document](https://geode.apache.org/docs/guide/113/managing/region_compression.html#topic_inm_whc_gl).

This example assumes you have installed JDK11 and GemFire.

## Steps

1. From the `gemfire-examples/compression` directory, build the example and
   run unit tests

        $ ../gradlew build

2. Next start the locator, two servers and create replicated region `example-region` with `compression` enabled.

        $ gfsh run --file=scripts/start.gfsh

3. Run the example to create and get entries using GemFire Java Client from the region

        $ ../gradlew run
        
4. Shut down the system:

        $ gfsh run --file=scripts/stop.gfsh
