# Copyright 2026 Broadcom. All Rights Reserved.
# classaskey example
This example shows how to use a developer created class as key as well as demonstrating using the IDataSerializable, ICacheableKey interfaces. The User class is used as a key and as such must be registered with the servers as a DataSerializable class with id number
 and has both a C# as well as Java type definitions.

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

1. From a command shell, set the current directory to the `classaskey` directory in your example workspace.

    ```console
    $ cd <install path>/examples/classaskey
    ```

1. Run script to start the Tanzu GemFire cluster, create a region and register DataSerializable type.
The startup script will register the User class on the servers by calling a function which executes InstantiateDSUser which registers the class with an id of 500.

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
Tom's Order: Cronut, Maple Bar,
Janet's Order: Bacon Maple Bar, Chocolate Cake,
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
