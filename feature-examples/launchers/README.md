<!--
  ~ Copyright 2024 Broadcom. All rights reserved.
  -->

# GemFire Launcher Example

This example shows how to use customized locator and server launcher with gemfire-bootstrap, which
provides class loader isolation and module support.

Examples for the `main` code needed to start the locator and server can be found in
[CustomLocatorApplication.java](src/main/java/com/vmware/gemfire/examples/launchers/CustomLocatorApplication.java)
and
[CustomServerApplication.java](src/main/java/com/vmware/gemfire/examples/launchers/CustomServerApplication.java).
Examples for running these launchers via Java can be found in the example scripts 
[start-locator.sh](scripts/start-locator.sh) and [start-server.sh](scripts/start-server.sh).

## Steps

1. From the `gemfire-examples/launchers` directory, build the `launchers.jar`.

        $ ../gradlew build

2. Start the locator using the example shell script. 

        $ scripts/start-locator.sh locator-1 12345

3. Start the server using the example shell script.

        $ scripts/start-locator.sh server-1 12346

You may need to edit the shell scripts for the paths and ports you intend to use.
