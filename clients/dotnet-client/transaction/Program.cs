// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.Transaction;

class Program
{
    private static readonly List<string> keys = new List<string>
    {
      "Key1",
      "Key2",
      "Key3",
      "Key4",
      "Key5",
      "Key6",
      "Key7",
      "Key8",
      "Key9",
      "Key10"
    };

    private static readonly List<int> values = new List<int>{ 33, 22, 44, 13, 23, 55, 77, 88, 99, 42 };


    static void Main(string[] args)
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("Transactions"))
        {
            Console.WriteLine("Created cache");

            IRegion<string, int> region = cache.CreateRegionFactory(RegionShortcut.PROXY)
              .Create<string, int>("exampleRegion");

            Console.WriteLine("Created region 'exampleRegion'");

            var retries = 5;
            while (retries-- > 0)
            {
                try
                {
                    cache.Begin();
                    var keysAndValues = keys.Zip(values, (k, v) => new { Key = k, Value = v });
                    foreach (var kv in keysAndValues)
                    {
                        region.Put(kv.Key, kv.Value);
                    }
                    cache.Commit();
                    Console.WriteLine("Committed transaction - exiting");
                    break;
                }
                catch
                {
                    cache.Rollback();
                    Console.WriteLine("Rolled back transaction - retrying({0})", retries);
                }
            }
        }
    }
}
