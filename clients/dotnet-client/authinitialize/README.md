# Copyright 2026 Broadcom. All Rights Reserved.
# authinitialize example
This example shows how to create and register a custom `IAuthInitialize` authentication
handler on the client that authenticates against a server that was started with the corresponding authenticator.
There are 3 Java files used to create a simple authenticator which are deployed to the server by the startup script.
The Java code demonstrates the basics required on the server side needed for the server authenticate the client.

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

1. From a command shell, set the current directory to the `authinitialize` directory in your example workspace.

    ```console
    $ cd <install path>/examples/authinitialize
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

1. Execute `dotnet run`.

    Expect the following output:

    ```console
    $ dotnet run
    ExampleAuthInitialize::ExampleAuthInitialize called
    ExampleAuthInitialize::GetCredentials called
    a = 1
    b = 2
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
