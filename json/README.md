<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
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