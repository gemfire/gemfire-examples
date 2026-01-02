// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ContinuousQuery
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var cacheFactory = new CacheFactory()
                .Set("log-level", "none");
            
            cacheFactory.AddLocator("localhost", 10334).SetSubscriptionEnabled(true);
            
            ICache cache = cacheFactory.Create("ContinuousQuery");

            Console.WriteLine("Registering for data serialization");

            cache.TypeRegistry.RegisterPdxType(Order.CreateDeserializable);

            IRegionFactory regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);

            IRegion<string, Order> orderRegion = regionFactory.Create<string, Order>("example_orderobject");

            IQueryService queryService = cache.GetQueryService();

            var cqAttributesFactory = cache.CreateCqAttributesFactory<string, Order>();

            var cqListener = new MyCqListener<string, Order>();

            var cqAttributes = cqAttributesFactory
                .AddCqListener(cqListener)
                .Create();
            try
            {
                var query = queryService.NewCq("MyCq", 
                    "SELECT * FROM /example_orderobject WHERE quantity > 30",
                    cqAttributes, false);

                Console.WriteLine("Executing continuous query");
                query.Execute();

                Console.WriteLine("Create orders");
                var order1 = new Order(1, "product x", 23);
                var order2 = new Order(2, "product y", 37);
                var order3 = new Order(3, "product z", 1);
                var order4 = new Order(4, "product z", 102);
                var order5 = new Order(5, "product x", 17);
                var order6 = new Order(6, "product z", 42);

                Console.WriteLine("Putting and changing Order objects in the region");
                orderRegion.Put("Order1", order1);
                orderRegion.Put("Order2", order2);
                orderRegion.Put("Order3", order3);
                orderRegion.Put("Order4", order4);
                orderRegion.Put("Order5", order5);
                orderRegion.Put("Order6", order6);

                orderRegion.Put("Order2", new Order(2, "product y", 45));
                orderRegion.Put("Order2", new Order(2, "product y", 29));
                orderRegion.Remove("Order6");

                System.Threading.Thread.Sleep(2000);

                query.Stop();
                query.Close();
            }
            catch (InvalidOperationException ex)
            {
                Console.WriteLine(ex.Message);
            }

            cache.Close();
        }
    }
}
