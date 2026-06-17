// Copyright 2026 Broadcom. All Rights Reserved.

// This example shows how to expose VMware Tanzu GemFire .NET Client metrics on a
// Prometheus endpoint. The client is instrumented with the Microsoft Metrics API
// (System.Diagnostics.Metrics), so OpenTelemetry's Prometheus exporter can scrape
// it directly. The app put/gets a key in a loop to generate stats.

using GemFire.Client;
using Microsoft.Extensions.Configuration;
using OpenTelemetry;
using OpenTelemetry.Metrics;

var config = new ConfigurationBuilder()
    .AddInMemoryCollection(new Dictionary<string, string?>
    {
        ["statistic-sampling-enabled"] = "true", // This property enables stats
        ["name"] = "test_cache", // A cache name is required as it becomes a part of a meter's name
        ["log-level"] = "none",
    })
    .Build();

var cache = new CacheFactory(config) // These are the default locator and server ports
    .AddLocator("localhost", 10334)
    .AddServer("localhost", 40404)
    .Create("prometheus-example");

var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);

try
{
    var region = regionFactory.Create<string, int>("test_region");

    using MeterProvider meterProvider = Sdk.CreateMeterProviderBuilder()
        .AddMeter("GemFire.Client.*") // All GemFire meters start with this string; the wildcard matches the rest.
        .AddView(instrument =>
            new MetricStreamConfiguration { Name = instrument.Meter.Name + "." + instrument.Name })
        .AddPrometheusHttpListener(options =>
        {
            options.Host = "localhost";
            options.Port = 9464;
        })
        .Build()!;

    Console.WriteLine("Take a look at the Prometheus endpoint: http://localhost:9464/metrics");
    Console.WriteLine("A Prometheus config is provided with the example. You can start Prometheus with \"prometheus --config.file=demo.yaml\"");
    Console.WriteLine("Press any key to exit");
    while (!Console.KeyAvailable)
    {
        Console.Write("\rPutting...");
        region.Put("key", 1);
        Thread.Sleep(500);
        Console.Write("\rGetting...");
        _ = region.Get("key");
        Thread.Sleep(500);
    }
}
catch
{
    Console.WriteLine("You need to first start the cluster. Run the provided startup script.");
}
