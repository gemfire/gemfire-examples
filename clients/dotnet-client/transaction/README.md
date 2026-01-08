# Copyright 2026 Broadcom. All Rights Reserved.
# Transaction example

This is a very simple example showing how to use TransactionManager.  This example shows
how to begin a transaction, commit a transaction, and rollback a transaction while showing
exception handling.  We commit two keys and rollback adding a third key and destroying an
existing key while showing how to handle exceptions.

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

1. From a command shell, set the current directory to the `transaction` directory in your example workspace.

    ```console
    $ cd workspace/examples/transaction
    ```

1. Run start script to start the Tanzu GemFire cluster and create a region.

   For Windows Powershell:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`

    ```console
    $ ./startlocator_servers.sh
    ```

1. Execute `dotnet run`. The output logs the cache and region creation, and the results of up to five attempts to commit the transaction.

    Example execution ends when the transaction is successfully committed, or when the maximum number of attempts is reached without a successful commit.

    ```console
    $dotnet run
    Created cache
    Created region 'exampleRegion'
    Rolled back transaction - retrying(4)
    Rolled back transaction - retrying(3)
    Rolled back transaction - retrying(2)
    Committed transaction - exiting
    ```

1. Run stop script to gracefully shutdown the Tanzu GemFire cluster.

   For Windows Powershell:

    ```console
    $ stopserver.ps1
    ```

    For Linux use `stoplocator_servers.sh`

    ```console
    $ ./stoplocator_servers.sh
    ```
