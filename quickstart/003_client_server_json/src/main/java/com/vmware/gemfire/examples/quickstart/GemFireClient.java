/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 */

package com.vmware.gemfire.examples.quickstart;

import java.util.Properties;
import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonDocumentFactory;
import org.apache.geode.json.JsonParseException;

// REQUIRES USING GemFire 10.0 or later

public class GemFireClient {

    public static void main(String[] args) {

        ClientCache cache;
        Region<Integer, JsonDocument> region;

        // Json pet records to be stored in GemFire
        String jsonPetRecord = "{" +
            " petname:\"Spot\"," +
            " idNum:0," +
            " breed:\"Poodle\"," +
            " owner:\"Bill\"," +
            " currentOnVaccines:true," +
            " issues:[\"needs special diet\",\"pulls on leash when walked\"]" +
        "}";

        String jsonPetRecord2 = "{" +
            " petname:\"Firehouse\"," +
            " idNum:1," +
            " breed:\"Dalmatian\"," +
            " currentOnVaccines:true," +
            " owner:\"Joe\"," +
            " issues:[\"bites mail person\"]" +
        "}";

        JsonDocument petRecord = null;
        JsonDocument petRecord2 = null;
        JsonDocument pt = null;

        // Configure ClientCache properties
        Properties clientCacheProps = new Properties();
        clientCacheProps.setProperty("log-level", "config");
        clientCacheProps.setProperty("log-file", "client.log");

        // Connect to Gemfire cluster
        cache = new ClientCacheFactory(clientCacheProps).addPoolLocator("127.0.0.1", 10334)
                .create();

        // Configure and create local proxy Region named "petrecords"
        region = cache.<Integer, JsonDocument>createClientRegionFactory(
                ClientRegionShortcut.PROXY).create("petrecords");

        // Get the default JsonDocumentFactory from the Cache (RegionService)
        JsonDocumentFactory jdf = cache.getJsonDocumentFactory();

        // Use JsonDocumentFactory to convert JSON to JsonDocument instances
        try {
            petRecord = jdf.create(jsonPetRecord);
            petRecord2 = jdf.create(jsonPetRecord2);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        // Put JSON documents into GemFire servers
        region.put(0, petRecord);
        region.put(1, petRecord2);

        // Get key 1 from servers returning a JsonDocument - Output JSON
        pt = region.get(1);
        System.out.println("JSON Pet Record: " + pt.toJson());

        // List the field names of the JsonDocument
        List list = (List) pt.getFieldNames();
        System.out.println("JSON Fields: " + list.toString());

        // Fetch field "petname" from JsonDocument
        System.out.println("PetName Field: " + pt.getField("petname"));

        // Fetch the Array field "issues" and display the first element
        System.out.println("Issues Field: " + ((List) (pt.getField("issues"))).get(0));

        // Compare two JSON documents
        if (pt.equals(petRecord2)) {
            System.out.println("Documents are equal");
        }

        // Cleanup and close ClientCache
        System.out.println();
        System.out.println("Closing Client");
        cache.close();
    }
}