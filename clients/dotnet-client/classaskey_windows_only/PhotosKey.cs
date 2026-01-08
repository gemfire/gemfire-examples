// Copyright 2024 Broadcom. All Rights Reserved.


using GemFire.Client;

namespace GemFire.Examples.ClassAsKey_Windows_Only
{
    public class PhotosKey : IDataSerializable, ICacheableKey
    {
        public List<String>? people;
        public DateTime rangeStart;
        public DateTime rangeEnd;

        // A default constructor is required for deserialization
        public PhotosKey() { }

        public PhotosKey(List<String> names, DateTime start, DateTime end)
        {
            people = names;

            // GemFire server defaults to Utc to ensure hashes match between client and
            // server
            // TimeZone tz = TimeZone.CurrentTimeZone;
            rangeStart = TimeZoneInfo.ConvertTimeToUtc(start);
            rangeEnd = TimeZoneInfo.ConvertTimeToUtc(end);
        }

        public override string ToString()
        {
            string result = "{";
#pragma warning disable CS8602 // Dereference of a possibly null reference.
            for (int i = 0; i < people.Count; i++)
            {
                result += people[i];
                if (i < people.Count - 1)
                    result += ", ";
            }
#pragma warning restore CS8602 // Dereference of a possibly null reference.
            result += "} from ";
            return result + rangeStart.ToString() + " to " +
              rangeEnd.ToString();
        }

        public void ToData(IDataOutput output)
        {
            output.WriteObject(people);
            output.WriteDate(rangeStart);
            output.WriteDate(rangeEnd);
        }

        public void FromData(IDataInput input)
        {
            people = (List<String>)input.ReadObject();
            rangeStart = (DateTime)input.ReadDate();
            rangeEnd = (DateTime)input.ReadDate();
        }

        public ulong ObjectSize
        {
            get { return 0; }
        }

        public bool Equals(ICacheableKey other)
        {
            return Equals((object)other);
        }

        public override bool Equals(object? obj)
        {
            if (obj == null) return false;

            if (this == obj)
            {
                return true;
            }

            if (GetType() != obj.GetType())
            {
                return false;
            }

            PhotosKey otherKey = (PhotosKey)obj;
            return (people == otherKey.people &&
              rangeStart == otherKey.rangeStart &&
              rangeEnd == otherKey.rangeEnd);
        }

        public override int GetHashCode()
        {
#pragma warning disable CS8604 // Possible null reference argument.
            return util.ObjectHash.Hash(people, rangeStart, rangeEnd);
#pragma warning restore CS8604 // Possible null reference argument.
        }

        public static ISerializable CreateDeserializable()
        {
            return new PhotosKey();
        }
    }
}
