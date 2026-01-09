// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ClassAsKey;

public class Program
{
    public static void Main()
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "none");
        cacheFactory.AddLocator("localhost", 10334);

        using (var cache = cacheFactory.Create("ClassAsKey"))
        {
            cache.TypeRegistry.RegisterType(User.CreateDeserializable, 500);
            cache.TypeRegistry.RegisterType(Order.CreateDeserializable, 501);

            var region = cache
                .CreateRegionFactory(RegionShortcut.PROXY)
                .Create<User, Order>("orders");

            var user1 = new User("Tom", 1);
            var user2 = new User("Janet", 2);
            var order1 = new Order();
            var order2 = new Order();

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
