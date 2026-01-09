// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ContinuousQuery;

public class MyCqListener<TKey, TResult> : ICqListener<TKey, TResult>
{
    public virtual void OnEvent(ICqEvent<TKey, TResult> ev)
    {
        var operationType = "UNKNOWN";

        switch (ev.QueryOperation)
        {
            case CqOperation.OP_TYPE_CREATE:
                operationType = "CREATE";
                break;
            case CqOperation.OP_TYPE_UPDATE:
                operationType = "UPDATE";
                break;
            case CqOperation.OP_TYPE_DESTROY:
                operationType = "DESTROY";
                break;
            default:
                Console.WriteLine("Unexpected operation encountered {0}", ev.QueryOperation);
                break;
        }

        var key = ev.Key;
        if (ev.NewValue is Order val)
        {
            Console.WriteLine("MyCqListener::OnEvent({0}) called with key {1}, value {2}", operationType, key, val.ToString());
        }
        else
        {
            Console.WriteLine("MyCqListener::OnEvent({0}) called with key {1}, value null", operationType, key);
        }
    }

    public virtual void OnError(ICqEvent<TKey, TResult> ev)
    {
        Console.WriteLine("MyCqListener::OnError called");
    }

    public virtual void Close()
    {
        Console.WriteLine("MyCqListener::close called");
    }
}
