// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.PDXAutoSerializer
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var cacheFactory = new CacheFactory()
                .Set("log-level", "none");
            cacheFactory.AddLocator("localhost", 10334);

            ICache cache = cacheFactory.Create("PDXAutoSerializer");

            Console.WriteLine("Registering for reflection-based auto serialization");
            var orderToRegister = new Order();
            cache.TypeRegistry.PdxSerializer = cache.CreateReflectionBasedAutoSerializer(orderToRegister.GetType().Namespace);

            IRegionFactory regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);

            IRegion<int, Order> orderRegion = regionFactory.Create<int, Order>("example_orderobject");

            Console.WriteLine("Storing order object in the region");

            const int orderKey = 65;

            var order = new Order(orderKey, "Vox AC30", 11);

            Console.WriteLine("order to put is " + order);
            orderRegion.Put(orderKey, order);

            Console.WriteLine("Successfully put order, getting now...");
            var orderRetrieved = orderRegion.Get(orderKey);

            Console.WriteLine("Order key: " + orderKey + " = " + orderRetrieved);

            cache.Close();
        }
    }
}


