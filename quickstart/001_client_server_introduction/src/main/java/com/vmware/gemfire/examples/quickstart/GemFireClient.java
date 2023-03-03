package com.vmware.gemfire.examples.quickstart;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.ClientCache;
import java.util.Properties;
import org.apache.geode.cache.Region;

public class GemFireClient {

    public static void main(String[] args) {
        
        String returnedValue;
        ClientCache cache;
        Region<Integer, String> region;

        // configure ClientCache properties
        Properties clientCacheProps = new Properties();
        clientCacheProps.setProperty("log-level", "config");
        clientCacheProps.setProperty("log-file", "client.log");

        // create cache with properties and configure locators for server discovery
        cache = new ClientCacheFactory(clientCacheProps).addPoolLocator("127.0.0.1", 10334)
                .create();

        // configure and create local proxy Region named example
        region = cache.<Integer, String>createClientRegionFactory(
                ClientRegionShortcut.PROXY).create("petnames");

        // create data
        region.put(0, "Spot");
        System.out.println("Put Key: " + 0 + " Value: " + "Spot");

        // retrieve data
        returnedValue = region.get(0);
        System.out.println("Returned value: " + returnedValue + " with key: " + 0);

        // retrieve key that doesn't exist - expect to see a null value
        returnedValue = region.get(1);
        System.out.println("Key-Value Not Found - Key: " + 1 + " Value: " + returnedValue);

        // update key
        region.put(0, "Firehouse");
        System.out.println("Update Key: " + 0 + " with new value: " + "Firehouse");
        returnedValue = region.get(0);
        System.out.println("Returned value: " + returnedValue + " with key: " + 0);

        // delete key and value
        region.destroy(0);
        returnedValue = region.get(0);
        System.out.println("Removed Key-Value no longer found - Key: " + 0 + " Value: " + returnedValue);

        // cleanup and close ClientCache
        System.out.println();
        System.out.println("Closing Client");
        cache.close();
    }
}
