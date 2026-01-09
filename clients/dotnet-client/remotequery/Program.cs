// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.RemoteQuery;

public class Program
{
    public static void Main()
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("RemoteQuery"))
        {
            Console.WriteLine("Registering for data serialization");

            cache.TypeRegistry.RegisterPdxType(Order.CreateDeserializable);

            var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
            var orderRegion = regionFactory.Create<string, Order>("custom_orders");

            Console.WriteLine("Create orders");
            var order1 = new Order(1, "product x", 23);
            var order2 = new Order(2, "product y", 37);
            var order3 = new Order(3, "product z", 1);
            var order4 = new Order(4, "product z", 102);
            var order5 = new Order(5, "product x", 17);
            var order6 = new Order(6, "product z", 42);

            Console.WriteLine("Storing orders in the region");
            orderRegion.Put("Order1", order1);
            orderRegion.Put("Order2", order2);
            orderRegion.Put("Order3", order3);
            orderRegion.Put("Order4", order4);
            orderRegion.Put("Order5", order5);
            orderRegion.Put("Order6", order6);

            var queryService = cache.GetQueryService();

            Console.WriteLine("Getting the orders from the region");
            var query = queryService.NewQuery<Order>("SELECT * FROM /custom_orders WHERE quantity > 30");
            var queryResults = query.Execute();

            Console.WriteLine("The following orders have a quantity greater than 30:");

            foreach (Order value in queryResults)
            {
                Console.WriteLine(value.ToString());
            }

        }
    }
}


