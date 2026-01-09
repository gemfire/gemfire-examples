// Copyright 2026 Broadcom. All Rights Reserved.


using GemFire.Client;

namespace GemFire.Examples.CacheListener;

class Program
{
    static void Main(string[] args)
    {
        var durable_id = "Reader-" + Guid.NewGuid().ToString();
        
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none")
            .Set("durable-client-id", durable_id)
            .Set("durable-timeout", "300s");

        cacheFactory.AddLocator("localhost", 10334)
            .SetSubscriptionEnabled(true);

        var listeningCache = cacheFactory.Create("CacheListener-durable-cache1");

        var cacheFactory2 = new CacheFactory()
            .Set("log-level", "none");

        cacheFactory2.AddLocator("localhost", 10334);

        var writingCache = cacheFactory2.Create("CacheListener-durable-cache2");

        var sampleListener = new MyCacheListener<string, string>();

        var region = listeningCache
            .CreateRegionFactory(RegionShortcut.PROXY)
            .SetCacheListener(sampleListener)
            .Create<string, string>("example_info");

        var region2 = writingCache
            .CreateRegionFactory(RegionShortcut.PROXY)
            .Create<string, string>("example_info");

        region.SubscriptionService.RegisterAllKeys();

        listeningCache.ReadyForEvents();

        Console.WriteLine("Create Keys a,b");
        region2.Put("a", "a");
        region2.Put("b", "b");

        Console.WriteLine("Update Keys a,b");
        region2.Put("a", "b");
        region2.Put("b", "c");

        Console.WriteLine("Remove Keys a,b");
        region2.Remove("a");
        region2.Remove("b");

        Console.WriteLine("Create Key c");
        region2.Put("c", "c");
        region2.Remove("c");

        writingCache.Close();
        listeningCache.Close();
    }
}
