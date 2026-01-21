# Copyright 2026 Broadcom. All Rights Reserved.
# remotequery example

This is a simple example showing how to execute a query on a remote region.

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

1. From a command shell, set the current directory to the `remotequery` directory in your example workspace.

    ```console
    $ cd workspace/examples/remotequery
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
    Registering for data serialization
    Create orders
    Storing orders in the region
    Getting the orders from the region
    The following orders have a quantity greater than 30:
    Order: [6, product z, 42]
    Order: [4, product z, 102]
    Order: [2, product y, 37]
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
