/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode_examples.luceneSpatial;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ExampleCommon {

  public static ClientCache createCache() {
    return new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).set("log-level", "WARN")
        .create();
  }

  public static Region<String, RegionInfo> createRegion(String regionName) {
    ClientCache cache = createCache();
    // create a local region that matches the server region
    return cache.<String, RegionInfo>createClientRegionFactory(ClientRegionShortcut.PROXY)
        .create(regionName);
  }

  public static LuceneService luceneService(Region<String, RegionInfo> region) {
    ClientCache cache = createCache();
    return LuceneServiceProvider.get(cache);
  }

  public static void closeCache() {
    createCache().close();
  }

  public static void putEntries(LuceneService luceneService, Map<String, RegionInfo> region)
      throws InterruptedException {
    region.put("McD1", new RegionInfo("McD1", -46.653, -23.543));
    region.put("McD2", new RegionInfo("McD2", -46.634, -23.5346));
    region.put("McD3", new RegionInfo("McD3", -46.613, -23.543));
    region.put("McD4", new RegionInfo("McD3", -46.614, -23.559));
    region.put("McD5", new RegionInfo("McD3", -46.631, -23.567));
    region.put("McD6", new RegionInfo("McD3", -46.653, -23.560));
    region.put("McD7", new RegionInfo("McD3", -46.653, -23.543));

    luceneService.waitUntilFlushed("simpleIndex2", "example-region-make-shape", 1,
        TimeUnit.MINUTES);
  }

  public static List<String> getLatLongList(Region<String, RegionInfo> region) {
    Set<String> keySet = region.keySetOnServer();
    List<String> LatLongList = new ArrayList<>(keySet);
    Collections.sort(LatLongList);
    return LatLongList;
  }
}

