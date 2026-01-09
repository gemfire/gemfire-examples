// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;
using System.Drawing;

namespace GemFire.Examples.ClassAsKey_Windows_Only;

public class PhotoMetaData : IDataSerializable
{
    public int fullResId;
    public Bitmap? thumbnailImage;

    public const int THUMB_WIDTH = 32;
    public const int THUMB_HEIGHT = 32;

    // A default constructor is required for deserialization
    public PhotoMetaData() { }

    public PhotoMetaData(int id, Bitmap thumb)
    {
        fullResId = id;
        thumbnailImage = thumb;
    }

    public void ToData(IDataOutput output)
    {
        output.WriteInt32(fullResId);

#pragma warning disable CS8602 // Dereference of a possibly null reference.
#pragma warning disable CA1416 // Validate platform compatibility
        for (int i = 0; i < thumbnailImage.Height; i++)
        {
            for (int j = 0; j < thumbnailImage.Width; j++)
            {
                output.WriteInt32(thumbnailImage.GetPixel(i, j).ToArgb());
            }
        }
#pragma warning restore CA1416 // Validate platform compatibility
#pragma warning restore CS8602 // Dereference of a possibly null reference.
    }

    public void FromData(IDataInput input)
    {
        fullResId = input.ReadInt32();

#pragma warning disable CA1416 // Validate platform compatibility
        thumbnailImage = new Bitmap(THUMB_WIDTH, THUMB_HEIGHT);
#pragma warning restore CA1416 // Validate platform compatibility
#pragma warning disable CA1416 // Validate platform compatibility
        for (int i = 0; i < thumbnailImage.Height; i++)
        {
            for (int j = 0; j < thumbnailImage.Width; j++)
            {
                thumbnailImage.SetPixel(i, j, System.Drawing.Color.FromArgb(input.ReadInt32()));
            }
        }
#pragma warning restore CA1416 // Validate platform compatibility
    }

    public ulong ObjectSize
    {
        get { return 0; }
    }

    public static ISerializable CreateDeserializable()
    {
        return new PhotoMetaData();
    }
}

public class PhotosValue : IDataSerializable
{
    public List<PhotoMetaData>? photosMeta;

    // A default constructor is required for deserialization
    public PhotosValue() { }

    public PhotosValue(List<PhotoMetaData> metaData)
    {
        photosMeta = metaData;
    }

    public void ToData(IDataOutput output)
    {
        output.WriteObject(photosMeta);
    }

    public void FromData(IDataInput input)
    {
        photosMeta = new List<PhotoMetaData>();
        if (input.ReadObject() is IList<object> pmd)
        {
            foreach (var item in pmd)
            {
                photosMeta.Add((PhotoMetaData)item);
            }
        }
    }

    public ulong ObjectSize
    {
        get { return 0; }
    }

    public static ISerializable CreateDeserializable()
    {
        return new PhotosValue();
    }
}


