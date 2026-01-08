// Copyright 2024 Broadcom. All Rights Reserved.

using System.Collections;
using functionexecution;
using GemFire.Client;

namespace GemFire.Examples.FunctionExecution
{
    class Program
    {
        static void Main(string[] args)
        {
            var cacheFactory = new CacheFactory()
                .Set("log-level", "none");
            cacheFactory.AddLocator("localhost", 10334);

            ICache cache = cacheFactory.Create("FunctionExecution");
            
            IRegionFactory regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            IRegion<object, object> region = regionFactory.Create<object, object>("partition_region");

            Console.WriteLine("Storing id and username in the region");

            string rtimmonsKey = "rtimmons";
            string rtimmonsValue = "Robert Timmons";
            string scharlesKey = "scharles";
            string scharlesValue = "Sylvia Charles";

            region.Put(rtimmonsKey, rtimmonsValue, null);
            region.Put(scharlesKey, scharlesValue, null);

            Console.WriteLine("Getting the user info from the region");
            var user1 = region.Get(rtimmonsKey, null);
            var user2 = region.Get(scharlesKey, null);

            Console.WriteLine(rtimmonsKey + " = " + user1);
            Console.WriteLine(scharlesKey + " = " + user2);

            ArrayList keyArgs = new ArrayList();
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

            cache.Close();
        }
    }
}
