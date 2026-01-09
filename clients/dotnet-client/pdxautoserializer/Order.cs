// Copyright 2026 Broadcom. All Rights Reserved.


namespace GemFire.Examples.PDXAutoSerializer;

public class Order
{
    public int OrderId { get; set; }
    public string? Name { get; set; }
    public short Quantity { get; set; }

    // A default constructor is required for reflection based autoserialization
    public Order() { }

    public Order(int orderId, string name, short quantity)
    {
        OrderId = orderId;
        Name = name;
        Quantity = quantity;
    }

    public override string ToString()
    {
        return "Order: [" + OrderId + ", " + Name + ", " + Quantity + "]";
    }
}


