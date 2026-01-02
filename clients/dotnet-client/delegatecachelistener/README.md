# Copyright 2026 Broadcom. All Rights Reserved.
# delegatecachelistener example

This example is similar to the cachelistener example and uses the `ICacheListener` interface.
The main change is that it makes use of a C# Delegate to allow replacement of the CacheListener on the region. 
This pattern can be used to change out the listener in a running client
or with minor changes can be used to add multiple listeners that will be triggered from cache events.

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

1. From a command shell, set the current directory to the `delegatecachelistener` directory in your example workspace.

    ```console
    $ cd <install path>/examples/delegatecachelistener
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
    Reader Durable ID: Reader-98b42580-1cce-42d7-87fb-f2b28483f670
    Writer Durable ID: Writer-fda52817-1e52-414d-9ef9-95fe4773f77b
    Default Delegate Constructor
    Default Constructor
    Delegate AfterRegionLive: VMware.GemFire.Client.ProtocolDriver.DotNetty.Events.RegionEvent`2[System.String,System.String]
    Delegate AfterCreate key: a value: a
    Delegate AfterUpdate key: a value: b
    Delegate AfterDestroy key: a
    Default Delegate2 Constructor
    Delegate2 AfterCreate key: a value: c
    Delegate2 AfterUpdate key: a value: d
    Delegate2 AfterDestroy key: a
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
