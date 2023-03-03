/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 */

package com.vmware.gemfire.examples.quickstart;

import java.util.Properties;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

public class GemFireClient {

    public static void main(String[] args) {

        Pet returnedPetValue;
        ClientCache cache;
        Region<Integer, Pet> region;

        // configure ClientCache properties
        Properties clientCacheProps = new Properties();
        clientCacheProps.setProperty("log-level", "config");
        clientCacheProps.setProperty("log-file", "client.log");

        // create cache with properties and configure locators for server discovery
        // use ReflectionBasedAutoSerializer for PDX serialization of Pet instances
        cache = new ClientCacheFactory(clientCacheProps).addPoolLocator("127.0.0.1", 10334)
                .setPdxSerializer(new ReflectionBasedAutoSerializer(
                        true,
                        "com.vmware.gemfire.examples.quickstart.Pet"
                        ))
                .create();

        // configure and create local proxy Region named pets
        region = cache.<Integer, Pet>createClientRegionFactory(
                ClientRegionShortcut.PROXY).create("pets");

        Pet pet = new Pet("Poodle", "Spot", "Bill");
        
        Pet pet2 = new Pet("Dalmatian", "Firehouse", "Joe");

        // Create - put a pet with key 0
        region.put(0, pet);
        region.put(1, pet2);
        
        // Retrieve - get a pet with key 0
        returnedPetValue = region.get(0);
        System.out.println("Returned pet: "+returnedPetValue.toString());

        // Update - put an updated pet with key petId
        pet.setOwner("Joyce");
        region.put(0, pet);
        returnedPetValue = region.get(0);
        System.out.println("Returned pet: "+returnedPetValue.toString());

        // delete - destroy a key:value with key 0
        region.destroy(0);
        System.out.println("Returned pet: "+region.get(0)); // null returned

        // cleanup and close ClientCache
        System.out.println();
        System.out.println("Closing Client");
        cache.close();
    }
}
