# Copyright 2026 Broadcom. All Rights Reserved.
# continuousquery example
This is a simple example showing how to execute a continuous query on a Tanzu GemFire region.

## Prerequisites
* Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/about_gemfire.html))
* Install [VMware Tanzu GemFire .NET Client](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-dotnet-client/1-0-beta/gf-dotnet-client/installation.html)
* Build examples from solution directory "dotnet build"
* Set `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
* Set `JAVA_HOME` to top of Java 8 JDK or Java 11 JDK installation

## Running
1. From a command shell, set the current directory to the `continuousquery` directory in your example workspace.

    ```console
    $ cd workspace/examples/continuousquery
    ```

1. Run script to start the Tanzu GemFire cluster with authentication and create a region.

   For Windows Powershell use `startserver.ps1`:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`

    ```console
    $ ./startlocator_servers.sh

1. Execute `dotnet run`. Expect the following output:

    ```console
    $ dotnet run
    Registering for data serialization
    Executing continuous query
    Create orders
    Putting and changing Order objects in the region
    MyCqListener::OnEvent(CREATE) called with key Order2, value Order: [2, product y, 37]
    MyCqListener::OnEvent(CREATE) called with key Order4, value Order: [4, product z, 102]
    MyCqListener::OnEvent(CREATE) called with key Order6, value Order: [6, product z, 42]
    MyCqListener::OnEvent(UPDATE) called with key Order2, value Order: [2, product y, 45]
    MyCqListener::OnEvent(DESTROY) called with key Order2, value Order: [2, product y, 29]
    MyCqListener::OnEvent(DESTROY) called with key Order6, value null
    MyCqListener::close called
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
