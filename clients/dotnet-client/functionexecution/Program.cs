// Copyright 2026 Broadcom. All Rights Reserved.

using System.Collections;
using GemFire.Client;

namespace GemFire.Examples.FunctionExecution;

class Program
{
    static void Main(string[] args)
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("FunctionExecution"))
        {
            var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            var region = regionFactory.Create<object, object>("partition_region");

            Console.WriteLine("Storing id and username in the region");

            var rtimmonsKey = "rtimmons";
            var rtimmonsValue = "Robert Timmons";
            var scharlesKey = "scharles";
            var scharlesValue = "Sylvia Charles";

            region.Put(rtimmonsKey, rtimmonsValue, null);
            region.Put(scharlesKey, scharlesValue, null);

            Console.WriteLine("Getting the user info from the region");
            var user1 = region.Get(rtimmonsKey, null);
            var user2 = region.Get(scharlesKey, null);

            Console.WriteLine(rtimmonsKey + " = " + user1);
            Console.WriteLine(scharlesKey + " = " + user2);

            var keyArgs = new ArrayList();
            keyArgs.Add(rtimmonsKey);
            keyArgs.Add(scharlesKey);

            var results = region.GetRegionFunctionService()
                .GetFunctionExecutor()
                .WithArgs(keyArgs)
                .WithCollector(new ExampleResultCollector())
                .Execute("ExampleMultiGetFunction", TimeSpan.FromMilliseconds(300))
                .GetResults(TimeSpan.FromMilliseconds(300));

            Console.WriteLine("Function Execution Results:");
            Console.WriteLine($"   Count = {results.Count}");

            foreach (List<object> item in results)
            {
                foreach (object item2 in item)
                {
                    Console.WriteLine("   value = {0}", item2.ToString());
                }
            }

        }
    }
}
