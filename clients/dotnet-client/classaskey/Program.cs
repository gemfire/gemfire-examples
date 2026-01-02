// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ClassAsKey
{
    public class Program
    {
        public static void Main()
        {
            var cacheFactory = new CacheFactory()
                .Set("log-level", "none");
            cacheFactory.AddLocator("localhost", 10334);

            ICache cache = cacheFactory.Create("ClassAsKey");

            cache.TypeRegistry.RegisterType(User.CreateDeserializable, 500);
            cache.TypeRegistry.RegisterType(Order.CreateDeserializable, 501);

            IRegion<User, Order> region = cache
                .CreateRegionFactory(RegionShortcut.PROXY)
                .Create<User, Order>("orders");

            User user1 = new User("Tom", 1);
            User user2 = new User("Janet", 2);
            Order order1 = new Order();
            Order order2 = new Order();

            order1.AddToOrder("Cronut");
            order1.AddToOrder("Maple Bar");
            order2.AddToOrder("Bacon Maple Bar");
            order2.AddToOrder("Chocolate Cake");

            region.Put(user1, order1);
            region.Put(user2, order2);
            Console.WriteLine(user1.Name + "\'s Order: " + region.Get(user1));
            Console.WriteLine(user2.Name + "\'s Order: " + region.Get(user2));
        }
    }
}
