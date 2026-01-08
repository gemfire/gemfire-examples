// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.AuthInitialize
{
    class Program
    {
        static void Main(string[] args)
        {
            var cacheFactory = new CacheFactory()
                .Set("log-level", "none")
                .SetAuthInitialize(new ExampleAuthInitialize());
            cacheFactory.AddLocator("localhost", 10334);

            ICache cache = cacheFactory.Create("ExampleAuthInit");

            IRegionFactory regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            IRegion<string, string> region = regionFactory.Create<string, string>("region");

            region.Put("a", "1");
            region.Put("b", "2");

            var a = region.Get("a");
            var b = region.Get("b");

            Console.Out.WriteLine("a = " + a);
            Console.Out.WriteLine("b = " + b);

            cache.Close();
        }
    }
}
