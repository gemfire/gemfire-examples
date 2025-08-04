// Copyright (c) VMware, Inc. 2023. All rights reserved.

package com.vmware.gemfire.examples.wanDelta;

import static org.apache.geode.cache.client.ClientRegionShortcut.CACHING_PROXY;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public class Example {
  private final Region<Integer, SimpleDelta> region;

  public Example(Region<Integer, SimpleDelta> region) {
    this.region = region;
  }

  public static void main(String[] args) {
    // connect to the locator in London cluster using port 10332
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10332)
        .set("log-level", "WARN").create();

    // create a local region that matches the server region
    Region<Integer, SimpleDelta> region = cache
        .<Integer, SimpleDelta>createClientRegionFactory(CACHING_PROXY).create("example-region");

    Example example = new Example(region);

    // create SimpleDelta instances with 10k payload
    int numEntries = 10;
    example.insertValues(numEntries, 10240);
    example.printValues("initially");

    // update SimpleDelta instances
    example.updateValues(numEntries);
    example.printValues("after update 1");

    // update SimpleDelta instances again
    example.updateValues(numEntries);
    example.printValues("after update 2");

    cache.close();
  }

  Set<Integer> getKeys() {
    return new HashSet<>(region.keySet());
  }

  void insertValues(int upperLimit, int payloadLength) {
    IntStream.rangeClosed(1, upperLimit)
        .forEach(i -> region.create(i, new SimpleDelta(i, new byte[payloadLength])));
  }

  void updateValues(int upperLimit) {
    IntStream.rangeClosed(1, upperLimit).forEach(i -> {
      SimpleDelta delta = region.get(i);
      delta.update(region);
    });
  }

  void printValues(String message) {
    Set<Map.Entry<Integer, SimpleDelta>> entries = region.entrySet();
    System.out.println(String.format("\nRegion %s contains the following %d entries %s:\n",
        region.getName(), region.size(), message));
    entries.stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(
        entry -> System.out.println(String.format("%d:%s", entry.getKey(), entry.getValue())));
  }
}
