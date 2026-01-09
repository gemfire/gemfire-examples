// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.PDXAutoSerializer;

public class Program
{
    public static void Main(string[] args)
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("PDXAutoSerializer"))
        {
            Console.WriteLine("Registering for reflection-based auto serialization");
            var orderToRegister = new Order();
            cache.TypeRegistry.PdxSerializer = cache.CreateReflectionBasedAutoSerializer(orderToRegister.GetType().Namespace);

            var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);

            var orderRegion = regionFactory.Create<int, Order>("example_orderobject");

            Console.WriteLine("Storing order object in the region");

            const int orderKey = 65;

            var order = new Order(orderKey, "Vox AC30", 11);

            Console.WriteLine("order to put is " + order);
            orderRegion.Put(orderKey, order);

            Console.WriteLine("Successfully put order, getting now...");
            var orderRetrieved = orderRegion.Get(orderKey);

            Console.WriteLine("Order key: " + orderKey + " = " + orderRetrieved);

        }
    }
}


