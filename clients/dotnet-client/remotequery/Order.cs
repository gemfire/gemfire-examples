// Copyright 2024 Broadcom. All Rights Reserved.


using GemFire.Client;

namespace GemFire.Examples.RemoteQuery
{
    public class Order : IPdxSerializable
    {
        private const string ORDER_ID_KEY_ = "order_id";
        private const string NAME_KEY_ = "name";
        private const string QUANTITY_KEY_ = "quantity";

        public long OrderId { get; set; }
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

        public void ToData(IPdxWriter output)
        {
            output.WriteLong(ORDER_ID_KEY_, OrderId);
            output.MarkIdentityField(ORDER_ID_KEY_);

            output.WriteString(NAME_KEY_, Name);
            output.MarkIdentityField(NAME_KEY_);

            output.WriteInt(QUANTITY_KEY_, Quantity);
            output.MarkIdentityField(QUANTITY_KEY_);
        }

        public void FromData(IPdxReader input)
        {
            OrderId = input.ReadLong(ORDER_ID_KEY_);
            Name = input.ReadString(NAME_KEY_);
            Quantity = (short)input.ReadInt(QUANTITY_KEY_);
        }

        public static IPdxSerializable CreateDeserializable()
        {
            return new Order();
        }
    }
}


