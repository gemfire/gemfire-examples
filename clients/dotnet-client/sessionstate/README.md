# Copyright 2026 Broadcom. All Rights Reserved.
# sessionstate example

This example shows how to back **ASP.NET Core session state** with VMware Tanzu
GemFire. `GemFire.Client.Session` provides `GemFireSessionStateCache`, an
[`IDistributedCache`](https://learn.microsoft.com/aspnet/core/performance/caching/distributed)
implementation that stores each session in a GemFire region. You register it with
`AddGemFireSessionStateCache(...)` alongside the standard `AddSession()` /
`UseSession()` middleware, and ASP.NET then reads and writes sessions through
GemFire transparently.

## Prerequisites

- Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/about_gemfire.html))
- Install [VMware Tanzu GemFire .NET Client](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-dotnet-client/1-0-beta/gf-dotnet-client/installation.html)
  - The .NET Client and its Session package are delivered as NuGet packages
  - Follow instructions in the solution README to install the packages
- Build examples from the solution directory: `dotnet build`
- Set shell variable `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
- Set shell variable `JAVA_HOME` to the top of a Java 17 JDK installation

## Running

1. From a command shell, set the current directory to the `sessionstate` directory in your example workspace.

    ```console
    $ cd workspace/examples/sessionstate
    ```

1. Run the script to start the Tanzu GemFire cluster and create the `exampleSessionState` region.

   For Windows Powershell use `startserver.ps1`:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`:

    ```console
    $ ./startlocator_servers.sh
    ```

1. Execute `dotnet run`. The app listens on `http://localhost:5050`.

    ```console
    $ dotnet run
    ```

   Port `5050` avoids the macOS AirPlay Receiver, which binds `5000` by default.
   To use a different port, set `ASPNETCORE_URLS` (e.g.
   `ASPNETCORE_URLS=http://localhost:8080 dotnet run`) and adjust the URLs below.

1. In another shell, exercise the session endpoints. The `-c`/`-b` cookie jar is
   what ties successive requests to the same session — without it each request
   gets a fresh session.

    ```console
    # Per-session counter increments on each request that shares the cookie jar
    $ curl -c cookies.txt -b cookies.txt http://localhost:5050/
    1
    $ curl -c cookies.txt -b cookies.txt http://localhost:5050/
    2

    # Store and read back an arbitrary value
    $ curl -b cookies.txt -X POST --data 'Tanzu' http://localhost:5050/session/team
    $ curl -b cookies.txt http://localhost:5050/session/team
    Tanzu

    # Remove it
    $ curl -b cookies.txt -X DELETE http://localhost:5050/session/team
    ```

   Each value round-trips through the GemFire `exampleSessionState` region. You can
   confirm entries land server-side with
   `gfsh -e "connect" -e "describe region --name=exampleSessionState"`.

1. Stop the app with `Ctrl+C`, then run the stop script to gracefully shut down the Tanzu GemFire cluster.

   For Windows Powershell:

    ```console
    $ stopserver.ps1
    ```

    For Linux use `stoplocator_servers.sh`:

    ```console
    $ ./stoplocator_servers.sh
    ```
