// Copyright 2026 Broadcom. All Rights Reserved.


using GemFire.Client;

namespace GemFire.Examples.PutGetRemove;

class Program
{
    static void Main(string[] args)
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("PutGetRemove"))
        {

            var region = cache
              .CreateRegionFactory(RegionShortcut.PROXY)
              .Create<string, string>("example_userinfo");

            Console.WriteLine("Storing id and username in the region");

            const string rtimmonsKey = "rtimmons";
            const string rtimmonsValue = "Robert Timmons";
            const string scharlesKey = "scharles";
            const string scharlesValue = "Sylvia Charles";

            region.Put(rtimmonsKey, rtimmonsValue);
            region.Put(scharlesKey, scharlesValue);

            Console.WriteLine("Getting the user info from the region");
            var user1 = region.Get(rtimmonsKey);
            var user2 = region.Get(scharlesKey);

            Console.WriteLine(rtimmonsKey + " = " + user1);
            Console.WriteLine(scharlesKey + " = " + user2);

            Console.WriteLine("Removing " + rtimmonsKey + " info from the region");

            if (region.Remove(rtimmonsKey))
            {
                Console.WriteLine("Info for " + rtimmonsKey + " has been deleted");
            }
            else
            {
                Console.WriteLine("Info for " + rtimmonsKey + " has not been deleted");
            }

        }
    }
}
