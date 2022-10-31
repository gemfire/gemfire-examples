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

## To run the JSON example

### Build the code

```
$ ../gradlew build
```

### Start a GemFire cluster with one locator and two servers

```
$ gfsh run --file=scripts/start.gfsh
```

### Run the example code

```
$ ../gradlew run
```

### Stop the GemFire cluster

```
$ gfsh run --file=scripts/stop.gfsh
```