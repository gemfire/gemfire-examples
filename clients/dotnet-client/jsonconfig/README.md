# Copyright 2026 Broadcom. All Rights Reserved.
# jsonconfig example
This example uses a JSON config file, converts the JSON into a .NET Dictionary for 
configuring `CacheFactory` `SystemProperties`
The example then creates two caches in the client, one that puts data into the
server and one that is listening for the cache update events from the server. 
The cache that is listening uses a custom class based on the `ICacheListener`
interface and registered with the Region to collect the received server events.

## Prerequisites

- Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/about_gemfire.html))
- Install [VMware Tanzu GemFire .NET Client](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-dotnet-client/1-0-beta/gf-dotnet-client/installation.html)
  - VMware Tanzu GemFire .NET Client is delivered as a NuGet package
  - Follow instructions in solution README to install package
- Build Java example classes (see solution README for details)
- Build examples from solution directory "dotnet build"
- Set shell variable `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
- Set shell variable `JAVA_HOME` to top of Java 8 JDK or Java 11 JDK installation

## Running

1. From a command shell, set the current directory to the `jsonconfig` directory in your example workspace.

    ```console
    $ cd <install path>/examples/jsonconfig
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
    AfterRegionLive Event: GemFire.Client.ProtocolDriver.DotNetty.Events.RegionEvent`2[System.String,System.String]
    AfterCreate Event key: CacheableBytes: System.Byte[]61 value: a
    Update Keys a,b
    AfterCreate Event key: CacheableBytes: System.Byte[]62 value: b
    AfterUpdate Event key: CacheableBytes: System.Byte[]61 value: b
    AfterUpdate Event key: CacheableBytes: System.Byte[]62 value: c
    Remove Keys a,b
    AfterDestroy Event key: CacheableBytes: System.Byte[]61
    AfterDestroy Event key: CacheableBytes: System.Byte[]62
    Create Key c
    AfterCreate Event key: CacheableBytes: System.Byte[]63 value: c
    AfterDestroy Event key: CacheableBytes: System.Byte[]63
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
