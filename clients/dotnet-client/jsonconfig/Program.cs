// Copyright 2026 Broadcom. All Rights Reserved.


using GemFire.Client;
using System.Text.Json;

namespace GemFire.Examples.JsonConfig;

class Program
{
    static void Main(string[] args)
    {
        var durable_id = "Reader-" + Guid.NewGuid().ToString();
        var durable_id2 = "Writer-" + Guid.NewGuid().ToString();

        // system properties in JSON config file
        var jsonString = File.ReadAllText("client.json");
        var config = JsonSerializer.Deserialize<Dictionary<string, string>>(jsonString)!;

        var cacheFactory = new CacheFactory(config);

        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("json-config-cache"))
        {
            var region = cache
                .CreateRegionFactory(RegionShortcut.PROXY)
                .Create<string, string>("example_info");

            Console.WriteLine("Create Keys a,b");
            region.Put("a", "a");
            region.Put("b", "b");

            Console.WriteLine("Update Keys a,b");
            region.Put("a", "b");
            region.Put("b", "c");

            Console.WriteLine("Remove Keys a,b");
            region.Remove("a");
            region.Remove("b");


        }
    }
}
