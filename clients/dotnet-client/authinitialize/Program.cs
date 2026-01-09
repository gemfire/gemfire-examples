// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.AuthInitialize;

class Program
{
    static void Main(string[] args)
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none")
            .SetAuthInitialize(new ExampleAuthInitialize());
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("ExampleAuthInit"))
        {
            var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            var region = regionFactory.Create<string, string>("region");

            region.Put("a", "1");
            region.Put("b", "2");

            var a = region.Get("a");
            var b = region.Get("b");

            Console.Out.WriteLine("a = " + a);
            Console.Out.WriteLine("b = " + b);

        }
    }
}
