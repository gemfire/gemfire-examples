# Copyright 2026 Broadcom. All Rights Reserved.
# sslputget example

This example illustrates how to use SSL encryption for all traffic between a .NET application and VMware Tanzu GemFire.

## Prerequisites

- Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/about_gemfire.html))
- Install [VMware Tanzu GemFire .NET Client](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-dotnet-client/1-0-beta/gf-dotnet-client/installation.html)
  - VMware Tanzu GemFire .NET Client is delivered as a NuGet package
  - Follow instructions in solution README to install package
- Build Java example classes (see solution README for details)
- Build examples from solution directory "dotnet build"
- Set shell variable `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
- Set shell variable `JAVA_HOME` to top of Java 8 JDK or Java 11 JDK installation

## Certificate Management

Install and use a trusted tool or write code to install certificate into local store.  Example uses dotnet-cert-tool \<trust is an issue here\>

```console
$ dotnet tool install dotnet-cert-tool --global

$ certificate-tool
GSoft.CertificateTool 1.0.0
Copyright (C) 2023 sumit10612

ERROR(S):
  No verb selected.

  add        Installs a pfx certificate to selected store.

  remove     Removes a pfx certificate from selected store.

  list       List all certificates in selected store.

  help       Display more information on a specific command.

  version    Display version information.
```

Add certificate. Sample certificates are provided in the keys directory.

```console
$ certificate-tool add -f .\keys\server.pfx -p password -s Root -l CurrentUser

```

## Running

1. From a command shell, set the current directory to the `sslputget` directory in your example workspace.

    ```console
    $ cd <install path>/examples/sslputget
    ```

1. Run the `startserver.ps1` script to start the Tanzu GemFire cluster with SSL and create a region.

   For Windows Powershell:

    ```console
    $ startserver.ps1
    ```

    For Linux:

    ```console
    $ ./startlocator_servers.sh
    ```

1. Execute `dotnet run`. Expect the following output:

    ```console
    > dotnet run
    Storing id and username in the region
    Getting the user info from the region
    rtimmons = Robert Timmons
    scharles = Sylvia Charles
    ```

1. Run the `stopserver.ps1` script to gracefully shutdown the Tanzu GemFire cluster.

   For Windows Powershell:

    ```console
    $ stopserver.ps1
    ```

    For Linux:

    ```console
    $ ./stoplocator_servers.sh
    ```
