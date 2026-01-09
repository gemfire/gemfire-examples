// Copyright 2026 Broadcom. All Rights Reserved.

using System.Collections;

namespace GemFire.Examples.ClassAsKey.util;

internal static class ObjectHash
{
    //Java frequently uses 31 as a prime factor to compute hash codes
    private static readonly int _HashingPrimeFactor = 31;

    /// <summary>
    /// Hashes consistent with java.util.Objects.hash(Object ...).
    /// </summary>
    /// <param name="value">
    /// Variable arguments to combine into hash.
    /// </param>
    public static int Hash(params object[] value)
    {
        var hashcode = 1;
        foreach (var item in value)
        {
            hashcode = (_HashingPrimeFactor * hashcode) + (item == null ? 0 : GetHashCode(item));
        }
        return hashcode;
    }

    /// <summary>
    /// Hashes consistent with java.util.Objects.hashCode(Object).
    /// </summary>
    /// <param name="value">
    /// Object to hash.
    /// </param>
    public static int GetHashCode(object value)
    {
        if (value == null)
            return 0;
        switch (value)
        {
            case float f:
                return GetHashCode(f);
            case double d:
                return GetHashCode(d);
            case sbyte b:
                return GetHashCode(b);
            case short s:
                return GetHashCode(s);
            case int i:
                return GetHashCode(i);
            case long l:
                return GetHashCode(l);
            case char c:
                return GetHashCode(c);
            case string ss:
                return GetHashCode(ss);
            case bool bb:
                return GetHashCode(bb);
            case DateTime dt:
                return GetHashCode(dt);
            case Array a:
                return GetHashCode(a);
            case IDictionary dd:
                return GetHashCode(dd);
            case ICollection cc:
                return GetHashCode(cc);
        }
        return value.GetHashCode();
    }

    /// <summary>
    /// Hashes consistent with java.util.Arrays.hashCode(Object[]) or
    /// java.util.List.hashCode().
    /// </summary>
    /// <param name="a">
    /// Array or List like collection to hash.
    /// </param>
    public static int GetHashCode(Array a)
    {
        if (a == null)
        {
            return 0;
        }

        var hashcode = 1;
        foreach (var item in a)
        {
            hashcode = (_HashingPrimeFactor * hashcode) + (item == null ? 0 : GetHashCode(item));
        }
        return hashcode;
    }

    /// <summary>
    /// Hashes consistent with java.lang.String.hashCode().
    /// </summary>
    /// <param name="value">
    /// String to hash.
    /// </param>
    /// <exception cref="ArgumentNullException">
    /// Null strings are not permitted.
    /// </exception>
    public static int GetHashCode(string value)
    {
        ArgumentNullException.ThrowIfNull(value);
        var hashCode = 0;
        for (var i = 0; i < value.Length; i++)
        {
            hashCode = (_HashingPrimeFactor * hashCode) + value[i];
        }
        return hashCode;
    }


    /// <summary>
    /// Hashes consistent with java.lang.Character.hashCode().
    /// </summary>
    /// <param name="value">
    /// Character to hash.
    /// </param>
    public static int GetHashCode(char value) => value;

    /// <summary>
    /// Hashes consistent with java.lang.Boolean.hashCode().
    /// </summary>
    /// <param name="value">
    /// Boolean to hash.
    /// </param>
    public static int GetHashCode(bool value) => value ? 1231 : 1237;

    /// <summary>
    /// Hashes consistent with java.lang.Byte.hashCode().
    /// </summary>
    /// <param name="value">
    /// Byte to hash.
    /// </param>
    public static int GetHashCode(sbyte value) => value;

    /// <summary>
    /// Hashes consistent with java.lang.Short.hashCode().
    /// </summary>
    /// <param name="value">
    /// Short to hash.
    /// </param>
    public static int GetHashCode(short value) => value;


    /// <summary>
    /// Hashes consistent with java.lang.Integer.hashCode().
    /// </summary>
    /// <param name="value">
    /// Integer to hash.
    /// </param>
    public static int GetHashCode(int value) => value;


    /// <summary>
    /// Hashes consistent with java.lang.Long.hashCode().
    /// </summary>
    /// <param name="value">
    /// Long to hash.
    /// </param>
    public static int GetHashCode(long value) => (int)(value & 0xffffffff) ^ (int)(value >> 32);

    /// <summary>
    /// Hashes consistent with java.lang.Float.hashCode().
    /// </summary>
    /// <param name="value">
    /// FLoat to hash.
    /// </param>
    public static int GetHashCode(float value)
    {
        if (float.IsNaN(value))
        {
            //see https://docs.oracle.com/javase/8/docs/api/java/lang/Float.html#floatToIntBits-float-
            // IEEE 754 spec NaN value
            return GetHashCode(0x7fc00000);
        }
        return BitConverter.ToInt32(BitConverter.GetBytes(value), 0);
    }

    /// <summary>
    /// Hashes consistent with java.lang.Double.hashCode().
    /// </summary>
    /// <param name="value">
    /// Double to hash.
    /// </param>
    public static int GetHashCode(double value)
    {
        if (double.IsNaN(value))
        {
            //see https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#doubleToLongBits-double-
            // IEEE 754 spec NaN value
            return GetHashCode(0x7ff8000000000000L);
        }
        return (int)(BitConverter.DoubleToInt64Bits(value) ^ (BitConverter.DoubleToInt64Bits(value) >> 32));
    }

    /// <summary>
    /// Hashes consistent with java.util.Date.hashCode() for UTC.
    /// </summary>
    /// <param name="value">
    /// Date to hash.
    /// </param>
    public static int GetHashCode(DateTime value)
    {
        DateTimeOffset dateTimeOffset = new(value, TimeSpan.Zero);
        var unixMilliseconds = dateTimeOffset.ToUnixTimeMilliseconds();

        return (int)(unixMilliseconds ^ (unixMilliseconds >> 32));
    }

    /// <summary>
    /// Hashes consistent with java.util.Arrays.hashCode(Object[]) or
    /// java.util.List.hashCode().
    /// </summary>
    /// <param name="value">
    /// Array or List like collection to hash.
    /// </param>
    public static int GetHashCode(ICollection value)
    {
        var hashcode = 1;
        foreach (var item in value)
        {
            hashcode = (_HashingPrimeFactor * hashcode) + (item == null ? 0 : GetHashCode(item));
        }
        return hashcode;
    }

    /// <summary>
    /// Hashes consistent with java.util.Map.hashCode().
    /// </summary>
    /// <param name="dictionary">
    /// Map to hash.
    /// </param>
    public static int GetHashCode(IDictionary dictionary)
    {
        var hashcode = 0;
        foreach (DictionaryEntry entry in dictionary)
        {
            hashcode += GetHashCode(entry.Key) ^ GetHashCode(entry.Value!);
        }
        return hashcode;
    }
}
