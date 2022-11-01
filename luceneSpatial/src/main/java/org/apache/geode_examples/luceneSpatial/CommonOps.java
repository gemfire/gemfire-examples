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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

public class CommonOps {

  public static ClientCache createCache() {
    return new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).set("log-level", "WARN")
        .create();
  }

  public static Region createClientRegion(String regionName) {
    ClientCache cache = createCache();
    // create a local region that matches the server region
    return cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);
  }

  public static LuceneService luceneService() {
    ClientCache cache = createCache();
    return LuceneServiceProvider.get(cache);
  }

  public static void closeCache() {
    createCache().close();
  }

  public static void putEntries(Map<String, LocationObject> region) {
    region.put("McDonalds1", new LocationObject("McDonalds1", -46.653, -23.543));
    region.put("McDonalds2", new LocationObject("McDonalds2", -46.634, -23.5346));
    region.put("McDonalds3", new LocationObject("McDonalds3", -46.613, -23.543));
    region.put("McDonalds4", new LocationObject("McDonalds4", -46.614, -23.559));
    region.put("McDonalds5", new LocationObject("McDonalds5", -46.631, -23.567));
    region.put("McDonalds6", new LocationObject("McDonalds6", -46.653, -23.560));
    region.put("McDonalds7", new LocationObject("McDonalds7", -46.653, -23.543));


  }

  public static List<String> getLatLongList(Region<String, LocationObject> region) {
    Set<String> keySet = region.keySetOnServer();
    List<String> LatLongList = new ArrayList<>(keySet);
    Collections.sort(LatLongList);
    return LatLongList;
  }
}
