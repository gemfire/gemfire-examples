// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;


namespace GemFire.Examples.AuthInitialize
{
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

        public IProperties<string, object> GetCredentials(IProperties<string, string> props, string server)
        {
            // TODO get your username and password
            Console.Out.WriteLine("ExampleAuthInitialize::GetCredentials called");

            var credentials = new GemFire.Client.Properties<string, object>();
            credentials.Insert("security-username", "root");
            credentials.Insert("security-password", "root");
            return credentials;
        }
    }
}
