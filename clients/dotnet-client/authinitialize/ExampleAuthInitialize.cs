// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;
using Microsoft.Extensions.Configuration;

namespace GemFire.Examples.AuthInitialize;

class ExampleAuthInitialize : IAuthInitialize
{
    public ExampleAuthInitialize()
    {
        // TODO initialize your resources here
        Console.Out.WriteLine("ExampleAuthInitialize::ExampleAuthInitialize called");
    }

    public void Close()
    {
        // TODO close your resources here
        Console.Out.WriteLine("ExampleAuthInitialize::Close called");
    }

    public IDictionary<string, object> GetCredentials(IConfiguration props, string server)
    {
        // TODO get your username and password
        Console.Out.WriteLine("ExampleAuthInitialize::GetCredentials called");

        var credentials = new Dictionary<string, object>();
        credentials.Add("security-username", "root");
        credentials.Add("security-password", "root");
        return credentials;
    }
}
