// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.DelegateCacheListener
{
    public class MyCacheListener<TKey, TValue> : ICacheListener<TKey, TValue>
    {
        private delegate void CloseDelegate(IRegion<TKey, TValue> region);
        private delegate void AfterRegionDisconnectedDelegate(IRegion<TKey, TValue> region);
        private delegate void AfterRegionLiveDelegate(IRegionEvent<TKey, TValue> ev);

        private delegate void AfterDestroyDelegate(IEntryEvent<TKey, TValue> ev);
        private delegate void AfterUpdateDelegate(IEntryEvent<TKey, TValue> ev);
        private delegate void AfterCreateDelegate(IEntryEvent<TKey, TValue> ev);
        CloseDelegate del1 = null!;
        AfterRegionDisconnectedDelegate del2 = null!;
        AfterRegionLiveDelegate del3 = null!;
        AfterDestroyDelegate del4 = null!;
        AfterUpdateDelegate del5 = null!;
        AfterCreateDelegate del6 = null!;
        public MyCacheListener(ICacheListener<TKey, TValue> CLDelegate)
        {
            System.Console.WriteLine("Default Constructor");
            ChangeListener(CLDelegate);
        }

        public void ChangeListener(ICacheListener<TKey, TValue> CLDelegate)
        {
            del3 = CLDelegate.AfterRegionLive;
            del4 = CLDelegate.AfterDestroy;
            del5 = CLDelegate.AfterUpdate;
            del6 = CLDelegate.AfterCreate;
        }

        public void Close(IRegion<TKey, TValue> region)
        {
            del1(region);
        }

        public void AfterRegionDisconnected(IRegion<TKey, TValue> region)
        {
            del2(region);
        }

        public void AfterRegionLive(IRegionEvent<TKey, TValue> ev)
        {
            del3(ev);
        }

        public void AfterRegionDestroy(IRegionEvent<TKey, TValue> ev) { }

        public void AfterRegionInvalidate(IRegionEvent<TKey, TValue> ev) { }

        public void AfterRegionClear(IRegionEvent<TKey, TValue> ev) { }

        public void AfterDestroy(IEntryEvent<TKey, TValue> ev)
        {
            del4(ev);
        }

        public void AfterInvalidate(IEntryEvent<TKey, TValue> ev) { }

        public void AfterUpdate(IEntryEvent<TKey, TValue> ev)
        {
            del5(ev);
        }

        public void AfterCreate(IEntryEvent<TKey, TValue> ev)
        {
            del6(ev);
        }
    }
}
