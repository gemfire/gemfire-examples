// Copyright 2026 Broadcom. All Rights Reserved.

using GemFire.Client;

using System.Drawing;


namespace GemFire.Examples.ClassAsKey_Windows_Only;

public class Program
{
    static ICache? cache;
    static readonly Random rand = new();

    public static void Main()
    {
        const int MAXPHOTOKEYS = 10;
        const int MAXPHOTOSPERKEY = 5;

        var photosMetaData = CreateRegion();

        Console.WriteLine("Populating the photos region\n");

        PhotosKey[] keys = new PhotosKey[MAXPHOTOKEYS];
        PhotosValue[] values = new PhotosValue[MAXPHOTOKEYS];
        int photoId = 0;

        for (int i = 0; i < MAXPHOTOKEYS; i++)
        {
            ChooseDateRange(out DateTime start, out DateTime end);
            keys[i] = new PhotosKey(ChoosePeople(), start, end);

            int numPhotos = rand.Next(0, MAXPHOTOSPERKEY + 1);
            List<PhotoMetaData> metaData = new();
            for (int j = 0; j < numPhotos; j++)
            {
                PhotoMetaData meta = new()
                {
                    fullResId = photoId++,
                    thumbnailImage = ChooseThumb()
                };
                metaData.Add(meta);
            }
            values[i] = new PhotosValue(metaData);

#pragma warning disable CS8604 // Possible null reference argument.
            Console.WriteLine("Inserting " + numPhotos + " photos for key: " + keys[i].ToString() +
              " with hashCode = " + new List<object> { keys[i].people, keys[i].rangeStart, keys[i].rangeEnd }.GetHashCode());
#pragma warning restore CS8604 // Possible null reference argument.

            photosMetaData.Put(keys[i], values[i]);
        }

        // Verify the region was populated properly
        photoId = 0;
        Console.WriteLine();
        for (int k = 0; k < MAXPHOTOKEYS; k++)
        {
            Console.WriteLine("Fetching photos for key: " + keys[k].ToString());

            var value = photosMetaData.Get(keys[k]);
            PhotoMetaData meta;
#pragma warning disable CS8602 // Dereference of a possibly null reference.
            for (int p = 0; p < value.photosMeta.Count; p++)
            {
                Console.WriteLine("   Fetching photo number " + p);

                meta = value.photosMeta[p];
                if (meta.fullResId != photoId)
                    Console.WriteLine("      ERROR: Expected fullResId = " + photoId + " but actual = " + meta.fullResId);

                bool thumbValid = true;
                for (int i = 0; i < PhotoMetaData.THUMB_HEIGHT; i++)
                {
                    for (int j = 0; j < PhotoMetaData.THUMB_WIDTH; j++)
                    {
#pragma warning disable CA1416 // Validate platform compatibility
                        if (meta.thumbnailImage.GetPixel(i, j) != values[k].photosMeta[p].thumbnailImage.GetPixel(i, j))
                        {
                            Console.WriteLine("      ERROR: Unexpected thumb for photoId = " + photoId);
                            thumbValid = false;
                            break;
                        }
#pragma warning restore CA1416 // Validate platform compatibility
                    }

                    if (!thumbValid)
                        break;
                }

                photoId++;
            }
#pragma warning restore CS8602 // Dereference of a possibly null reference.
        }

#pragma warning disable CS8602 // Dereference of a possibly null reference.
        cache.Close();
#pragma warning restore CS8602 // Dereference of a possibly null reference.

        Console.ReadLine();
    }

    public static IRegion<PhotosKey, PhotosValue> CreateRegion()
    {
        var cacheFactory = new CacheFactory()
            .Set("log-level", "debug")
            .Set("log-file", "classaskey.log");
        cacheFactory.AddLocator("localhost", 10334);

        cache = cacheFactory.Create("ClassAsKey-windows");

        Console.WriteLine("Registering for data serialization");

        cache.TypeRegistry.RegisterType(PhotosKey.CreateDeserializable, 500);
        cache.TypeRegistry.RegisterType(PhotosValue.CreateDeserializable, 501);
        cache.TypeRegistry.RegisterType(PhotoMetaData.CreateDeserializable, 502);

        var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);

        var photosMetaData = regionFactory.Create<PhotosKey, PhotosValue>("photosMetaData");
        return photosMetaData;
    }

    public static List<String> ChoosePeople()
    {
        List<String> availablePeople = new()
        {
            "Alice",
            "Bob",
            "Carol",
            "Ted"
        };

        List<String> chosenPeople = new();

        // Choose at least one person
        var numChosen = rand.Next(1, availablePeople.Count + 1);

        int index;
        var numAvailable = availablePeople.Count;

        for (int i = 0; i < numChosen; i++)
        {
            // Choose someone not already chosen
            index = rand.Next(numAvailable);
            chosenPeople.Add(availablePeople[index]);

            // Update available people
            availablePeople.RemoveAt(index);
            numAvailable--;
        }

        // Sort the chosen. We only care who is chosen, not the order they're chosen.
        chosenPeople.Sort();
        return chosenPeople;
    }

    public static void ChooseDateRange(out DateTime start, out DateTime end)
    {
        //Choose start and end dates between Jan 1, 1970 and now
        var earliestStart = new DateTime(1970, 1, 1);
        var numAvailableDays = (int)(DateTime.Now - earliestStart).TotalDays;

        var startIndex = rand.Next(numAvailableDays);
        start = earliestStart.AddDays(startIndex);

        var numRemainingDays = (int)(DateTime.Now - start).TotalDays;
        end = start.AddDays(rand.Next(numRemainingDays));
    }

    public static Bitmap ChooseThumb()
    {
        int thumbWidth = 32;
        int thumbHeight = 32;

#pragma warning disable CA1416 // Validate platform compatibility
        Bitmap thumb = new(thumbWidth, thumbHeight);
#pragma warning restore CA1416 // Validate platform compatibility
        for (int j = 0; j < thumbHeight; j++)
        {
            for (int i = 0; i < thumbWidth; i++)
            {
#pragma warning disable CA1416 // Validate platform compatibility
                thumb.SetPixel(i, j, Color.FromArgb(rand.Next(256), rand.Next(256), rand.Next(256)));
#pragma warning restore CA1416 // Validate platform compatibility
            }
        }

        return thumb;
    }
}


