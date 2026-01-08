# Copyright 2026 Broadcom. All Rights Reserved.
# putgetremove example

This is a very simple example showing how to create a `Cache` using the `CacheFactory`,
configure a `Pool` with a `PoolFactory`, and configure a `Region` with a `RegionFactory`.
We then put, get, and remove some primitive data in the region.

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

1. From a command shell, set the current directory to the `putgetremove` directory in your example workspace.

    ```console
    $ cd workspace/examples/putgetremove
    ```

1. Run script to start the Tanzu GemFire cluster with authentication and create a region.

   For Windows Powershell use `startserver.ps1`:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`

    ```console
    $ ./startlocator_servers.sh
    ```

1. Execute `dotnet run`. Expect the following output:

    ```console
    $ dotnet run
    Storing id and username in the region
    Getting the user info from the region
    rtimmons = Robert Timmons
    scharles = Sylvia Charles
    Removing rtimmons info from the region
    Info for rtimmons has been deleted
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
