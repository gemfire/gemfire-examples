// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ClassAsKey
{
    public class Order : IDataSerializable
    {
        List<object>? donuts;
        public Order()
        {
        }

        public void AddToOrder(string item)
        {
            if (donuts == null)
            {
                donuts = new List<object>();
                donuts.Add(item);
            }
            else
            {
                donuts.Add(item);
            }
        }
        public void ToData(IDataOutput output)
        {
            output.WriteObject(donuts);
        }

        public void FromData(IDataInput input)
        {
            donuts = input.ReadObject();
        }

        public static ISerializable CreateDeserializable()
        {
            return new Order();
        }

        override public String ToString()
        {
            if (donuts == null)
            {
                return "";
            }
            else
            {
                string donutlist = "";
                foreach (string donut in donuts)
                {
                    donutlist += donut + ", ";
                }
                return donutlist;
            }
        }
    }
}
