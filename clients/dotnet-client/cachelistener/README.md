# Copyright 2026 Broadcom. All Rights Reserved.
# cachelistener example
This example creates two caches in the client, one that puts data into the
server and one that is listening for the cache update events from the server. The cache that is listening uses a custom class based on the `ICacheListener`
interface and registered with the Region to collect the received server events.

## Prerequisites

- Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://docs.vmware.com/en/VMware-GemFire/index.html))
- Install [VMware Tanzu GemFire .NET Client](INSTALLATION.md)
  - VMware Tanzu GemFire .NET Client is delivered as a NuGet package
  - Follow instructions in solution README to install package
- Build Java example classes (see solution README for details)
- Build examples from solution directory "dotnet build"
- Set shell variable `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
- Set shell variable `JAVA_HOME` to top of Java 8 JDK or Java 11 JDK installation

## Running

1. From a command shell, set the current directory to the `cachelistener` directory in your example workspace.

    ```console
    $ cd <install path>/examples/cachelistener
    ```

1. Run script to start the Tanzu GemFire cluster and create a region.

   For Windows Powershell use `startserver.ps1`:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`

    ```console
    $ ./startlocator_servers.sh
    ```

1. Execute `dotnet run`.

    Expect the following output:

    ```console
    $ dotnet run
    Create Keys a,b
    AfterRegionLive Event: VMware.GemFire.Client.ProtocolDriver.DotNetty.Events.RegionEvent`2[System.String,System.String]
    Update Keys a,b
    Remove Keys a,b
    AfterCreate Event key: a value: a
    AfterCreate Event key: b value: b
    Create Key c
    AfterUpdate Event key: a value: b
    ```

1. Run the stop script to gracefully shutdown the Tanzu GemFire cluster.

   For Windows Powershell:

    ```console
    $ stopserver.ps1
    ```

    For Linux use `stoplocator_servers.sh`

    ```console
    $ ./stoplocator_servers.sh
    ```
