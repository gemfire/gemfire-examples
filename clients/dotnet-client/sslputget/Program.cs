// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.SSLPutGet;

class Program
{
    static void Main(string[] args)
    {
        var baseDirectory = AppContext.BaseDirectory;
        var projectDirectory = Path.GetFullPath(Path.Combine(baseDirectory, @"..\..\..\"));

        var cacheFactory = new CacheFactory()
          .Set("log-level", "none")
          .Set("ssl-enabled", "true")
          .Set("ssl-keystore", projectDirectory + "keys\\client.pfx")
          .Set("ssl-keystore-password", "password")
          .Set("ssl-truststore", projectDirectory + "keys\\ca.pfx")
          .Set("ssl-truststore-password", "password");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("SSLPutGet"))
        {
            IRegionFactory regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            IRegion<object, object> region = regionFactory.Create<object, object>("testSSLRegion");

            Console.WriteLine("Storing id and username in the region");

            var rtimmonsKey = "rtimmons";
            var rtimmonsValue = "Robert Timmons";
            var scharlesKey = "scharles";
            var scharlesValue = "Sylvia Charles";

            region.Put(rtimmonsKey, rtimmonsValue);
            region.Put(scharlesKey, scharlesValue);

            Console.WriteLine("Getting the user info from the region");
            var user1 = region.Get(rtimmonsKey);
            var user2 = region.Get(scharlesKey);

            Console.WriteLine(rtimmonsKey + " = " + user1);
            Console.WriteLine(scharlesKey + " = " + user2);

        }
    }
}
