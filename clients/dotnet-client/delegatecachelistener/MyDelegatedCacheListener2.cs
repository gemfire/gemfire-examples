// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.DelegateCacheListener;

public class MyDelegatedCacheListener2<TKey, TValue> : ICacheListener<TKey, TValue>
{
    public MyDelegatedCacheListener2()
    {
        System.Console.WriteLine("Default Delegate2 Constructor");
    }

    public void Close(IRegion<TKey, TValue> region)
    {
        Console.WriteLine("Delegate2 Close");
    }

    public void AfterRegionDisconnected(IRegion<TKey, TValue> region)
    {
        Console.WriteLine("Delegate2 AfterRegionDisconnected");
    }


    public void AfterRegionLive(IRegionEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterRegionLive");
    }


    public void AfterRegionDestroy(IRegionEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterRegionDestroy");
    }


    public void AfterRegionInvalidate(IRegionEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate AfterRegionInvalidate");
    }


    public void AfterRegionClear(IRegionEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterRegionClear");
    }


    public void AfterDestroy(IEntryEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterDestroy key: " + ev.Key);
    }


    public void AfterInvalidate(IEntryEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterInvalidate");
    }


    public void AfterUpdate(IEntryEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterUpdate key: " + ev.Key + " value: " + ev.NewValue);
    }


    public void AfterCreate(IEntryEvent<TKey, TValue> ev)
    {
        Console.WriteLine("Delegate2 AfterCreate key: " + ev.Key + " value: " + ev.NewValue);
    }

}
