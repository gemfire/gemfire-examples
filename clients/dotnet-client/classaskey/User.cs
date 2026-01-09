// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace GemFire.Examples.ClassAsKey
{
    public class User : IDataSerializable, ICacheableKey
    {
        public string? Name { get; set; }
        int Id;

        public User()
        {
        }
        public User(string name, int id)
        {
            Name = name;
            Id = id;
        }
        public void ToData(IDataOutput output)
        {
            output.WriteString(Name);
            output.WriteInt32(Id);
        }

        public void FromData(IDataInput input)
        {
            Name = input.ReadString();
            Id = input.ReadInt32();
        }
        public static ISerializable CreateDeserializable()
        {
            return new User();
        }
        public override int GetHashCode()
        {
            return util.ObjectHash.Hash(Name!, Id);
        }
        public override String ToString()
        {
            return $"User - Name {Name} Id {Id}";
        }
    }

}
