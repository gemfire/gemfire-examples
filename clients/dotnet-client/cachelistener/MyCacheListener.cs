// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.CacheListener;

public class MyCacheListener<TKey, TValue> : ICacheListener<TKey, TValue>
{
    public MyCacheListener()
    {
    }

    public void Close(IRegion<TKey, TValue> region)
    {
        System.Console.WriteLine("Close: " + region.ToString());
    }

    public void AfterRegionDisconnected(IRegion<TKey, TValue> region)
    {
        System.Console.WriteLine("AfterRegionDisconnected: " + region.ToString());
    }

    public void AfterRegionLive(IRegionEvent<TKey, TValue> ev)
    {
        //System.Console.WriteLine("AfterRegionLive Event: " + ev.Region.Name); adding this line causes output to disappear
        System.Console.WriteLine("AfterRegionLive Event: " + ev.ToString());
    }

    public void AfterRegionDestroy(IRegionEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterRegionDestroy" + ev.ToString());
    }

    public void AfterRegionInvalidate(IRegionEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterRegionInvalidate: " + ev.ToString()); ;
    }

    public void AfterRegionClear(IRegionEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterRegionClear: " + ev.ToString());
    }

    public void AfterDestroy(IEntryEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterDestroy Event key: " + ev.Key);
    }
    public void AfterInvalidate(IEntryEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterInvalidate: " + ev.ToString());
    }

    public void AfterUpdate(IEntryEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterUpdate Event key: " + ev.Key + " value: " + ev.NewValue);
    }

    public void AfterCreate(IEntryEvent<TKey, TValue> ev)
    {
        System.Console.WriteLine("AfterCreate Event key: " + ev.Key + " value: " + ev.NewValue);
    }
}
