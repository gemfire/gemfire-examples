/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 /
 * (the "License"); you may not use this file except in compliance with the License. You may /
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the /
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, /
 * either / express or implied. See the License for the specific language governing permissions and
 * limitations / under the License.
 */
package org.apache.geode_examples.luceneSpatial;

/*
 * The example shows if one place resides in the other place
 */


import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

public class SearchOverlappingLocation {
  private static final String REGION = "locationRegion";
  private static final String INDEX = "simpleIndex";
  static double maxLong = -46.614;
  static double maxLat = 23.559;
  static double minLong = -46.653;
  static double minLat = -23.543;

  public static void findLocationWithinRectangle()
      throws LuceneQueryException, InterruptedException {
    // Create client region which is same as the region on the server

    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();
    Region<String, LocationObject> region =
        cache.<String, LocationObject>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create(REGION);

    // Create Lucene Service
    LuceneService luceneService = LuceneServiceProvider.get(cache);
    // Add some entries into the region
    CommonOps.putEntries(region);
    // indexing on the given region
    luceneService.waitUntilFlushed(INDEX, region.getName(), 1, TimeUnit.MINUTES);

    // query a location that is inside the rectangular shape
    LuceneQuery<Integer, LocationObject> query = luceneService.createLuceneQueryFactory()
        .create(INDEX, region.getName(), index -> SpatialHelper
            .findLocationThatIsInsideTheRectangle(minLong, minLat, maxLong, maxLat));
    System.out
        .println("3. Found Coordinates that are inside the rectangle : " + query.findValues());
    // Close the cache
    CommonOps.closeCache();

  }

}
