// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.DelegateCacheListener
{
    public class MyDelegatedCacheListener<TKey, TValue> : ICacheListener<TKey, TValue>
    {
        public MyDelegatedCacheListener()
        {
            System.Console.WriteLine("Default Delegate Constructor");
        }

        public void Close(IRegion<TKey, TValue> region)
        {
            Console.WriteLine("Delegate Close");
        }

        public void AfterRegionDisconnected(IRegion<TKey, TValue> region)
        {
            Console.WriteLine("Delegate AfterRegionDisconnected: " + region.ToString());
        }


        public void AfterRegionLive(IRegionEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterRegionLive: " + ev.ToString());
        }


        public void AfterRegionDestroy(IRegionEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterRegionDestroy");
        }


        public void AfterRegionInvalidate(IRegionEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterRegionInvalidate");
        }


        public void AfterRegionClear(IRegionEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterRegionClear");
        }


        public void AfterDestroy(IEntryEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterDestroy key: " + ev.Key);
        }


        public void AfterInvalidate(IEntryEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterInvalidate");
        }


        public void AfterUpdate(IEntryEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterUpdate key: " + ev.Key + " value: " + ev.NewValue);
        }


        public void AfterCreate(IEntryEvent<TKey, TValue> ev)
        {
            Console.WriteLine("Delegate AfterCreate key: " + ev.Key + " value: " + ev.NewValue);
        }

    }
}
