# Copyright 2026 Broadcom. All Rights Reserved.
# prometheus example

This example shows how to expose VMware Tanzu GemFire .NET Client metrics on a
Prometheus endpoint and view them. The app puts and gets a key/value twice a
second to generate stats. The demo depends on [OpenTelemetry](https://opentelemetry.io/),
an open-source metrics framework.

The GemFire .NET Client is instrumented with the
[Microsoft Metrics API](https://learn.microsoft.com/dotnet/core/diagnostics/metrics),
so any compatible tool (for example `dotnet-counters`) also works. The client
exports *meters*, and meters export *instruments*. Every meter name begins with
`GemFire.Client.`, then is subdivided by cache name and component (cache, region,
pool, etc.), and then by specific operation — for example
`GemFire.Client.test_cache.test_region`.

## Prerequisites

- Install VMware Tanzu GemFire (see [VMware Tanzu GemFire documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-2/gf/about_gemfire.html))
- Install [VMware Tanzu GemFire .NET Client](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-dotnet-client/1-0-beta/gf-dotnet-client/installation.html)
  - The .NET Client is delivered as a NuGet package; follow the solution README to install it
- Install [Prometheus](https://prometheus.io/) and add it to your `PATH`
- Build examples from the solution directory: `dotnet build`
- Set shell variable `GEMFIRE_HOME` to the install directory of VMware Tanzu GemFire
- Set shell variable `JAVA_HOME` to the top of a Java 17 JDK installation

## Running

1. From a command shell, set the current directory to the `prometheus` directory in your example workspace.

    ```console
    $ cd workspace/examples/prometheus
    ```

1. Run the script to start the Tanzu GemFire cluster and create the `test_region` region.

   For Windows Powershell use `startserver.ps1`:

    ```console
    $ startserver.ps1
    ```

    For Linux use `startlocator_servers.sh`:

    ```console
    $ ./startlocator_servers.sh
    ```

1. Start the demo app.

    ```console
    $ dotnet run
    ```

   The app exports metrics at `http://localhost:9464/metrics`. The endpoint is
   static, so refresh to see the values change.

1. Start your Prometheus instance against the provided scrape config.

    ```console
    $ prometheus --config.file=demo.yaml
    ```

   The Prometheus console can be viewed at the default location `http://localhost:9090/`.

1. On the Prometheus console, select the `Graph` tab, click the metric-explorer
   (globe) button next to `Execute`, choose
   `GemFire_Client_test_cache_test_region_get_count_total`, and press `Execute`.
   (Prometheus replaces the `.` separators in meter names with `_`.)

1. Stop the app with any keypress, then run the stop script to gracefully shut down the Tanzu GemFire cluster.

   For Windows Powershell:

    ```console
    $ stopserver.ps1
    ```

    For Linux use `stoplocator_servers.sh`:

    ```console
    $ ./stoplocator_servers.sh
    ```

## Further Resources

- [Microsoft's documentation](https://learn.microsoft.com/dotnet/core/diagnostics/metrics) on collecting metrics
- [OpenTelemetry](https://opentelemetry.io/)
