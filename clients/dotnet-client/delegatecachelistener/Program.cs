// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.DelegateCacheListener
{
    class Program
    {
        static void Main(string[] args)
        {
            string durable_id = "Reader-" + Guid.NewGuid().ToString();
            string durable_id2 = "Writer-" + Guid.NewGuid().ToString();

            var cacheFactory = new CacheFactory()
                .Set("log-level", "none")
                .Set("durable-client-id", durable_id);

            cacheFactory.AddLocator("localhost", 10334)
                .SetSubscriptionEnabled(true);

            ICache cache = cacheFactory.Create("CacheListener-durable-cache1");

            var cacheFactory2 = new CacheFactory()
                .Set("log-level", "none")
                .Set("durable-client-id", durable_id2);

            cacheFactory2.AddLocator("localhost", 10334);

            ICache cache2 = cacheFactory2.Create("CacheListener-durable-cache2");

            MyDelegatedCacheListener<string, string> myDelegatedCacheListener = new MyDelegatedCacheListener<string, string>();
            MyCacheListener<string, string> sampleListener = new MyCacheListener<string, string>(myDelegatedCacheListener);

            IRegion<string, string> region = cache
              .CreateRegionFactory(RegionShortcut.PROXY)
              .SetCacheListener(sampleListener)
              .Create<string, string>("example_info");

            IRegion<string, string> region2 = cache2
              .CreateRegionFactory(RegionShortcut.PROXY)
              .Create<string, string>("example_info");

            region.SubscriptionService.RegisterAllKeys();

            cache.ReadyForEvents();

            region2.Put("a", "a");
            region2.Put("a", "b");
            region2.Remove("a");
            Task.Delay(2000).Wait(); //wait for events before changing listener
            MyDelegatedCacheListener2<string, string> myDelegatedCacheListener2 = new MyDelegatedCacheListener2<string, string>();
            sampleListener.ChangeListener(myDelegatedCacheListener2);
            region2.Put("a", "c");
            region2.Put("a", "d");
            region2.Remove("a");
            cache.Close();
            cache2.Close();
        }
    }
}
