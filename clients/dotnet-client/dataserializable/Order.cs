// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.Dataserializable
{
    public class Order : IDataSerializable
    {
        public int OrderId { get; set; }
        public string? Name { get; set; }
        public short Quantity { get; set; }

        // A default constructor is required for deserialization
        public Order() { }

        public Order(int orderId, string name, short quantity)
        {
            OrderId = orderId;
            Name = name;
            Quantity = quantity;
        }

        public override string ToString()
        {
            return string.Format("Order: [{0}, {1}, {2}]", OrderId, Name, Quantity);
        }

        public void ToData(IDataOutput output)
        {
            output.WriteInt32(OrderId);
            //output.WriteUTF(Name);
            output.WriteString(Name);
            output.WriteInt16(Quantity);
        }

        public void FromData(IDataInput input)
        {
            OrderId = input.ReadInt32();
            //Name = input.ReadUTF();
            Name = input.ReadString();
            Quantity = input.ReadInt16();
        }

        public ulong ObjectSize
        {
            get { return 0; }
        }

        public static ISerializable CreateDeserializable()
        {
            return new Order();
        }
    }
}


