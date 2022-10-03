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

import org.apache.commons.lang3.StringUtils;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/*
 * This example shows if a location is within a given area
 */
public class GeoLocationSearchWithinARegionExample {
  private static final String INDEX_NAME = "simpleIndex2";
  private static final String REGION_NAME = "example-region2";

  public static void verifyIfGivenLocationIsInsideShape()
      throws LuceneQueryException, InterruptedException {
    Region<String, PlaceAndShapeObject> region = createClientRegion();
    putEntries(region);

    // Create Lucene Service
    LuceneService luceneService = CommonOps.luceneService();
    luceneService.waitUntilFlushed(INDEX_NAME, region.getName(), 1, TimeUnit.MINUTES);

    double givenLongitude = -46.653;
    double givenLatitude = -23.543;

    Set<String> keySet = region.keySetOnServer();
    for (String key : keySet) {
      // build a polygon from the coordinates
      String r = (StringUtils.substringBetween(region.get(key).getShape(), "((", "))"));
      String[] r1 = r.split(" ");

      String verifyGivenLocation =
          StringUtils.substringBefore(
              String.valueOf(
                  SpatialHelper.verifyLocationIsInsideShape(givenLongitude, givenLatitude, r1)),
              ":");
      if (verifyGivenLocation.equalsIgnoreCase("true")) {
        LuceneQuery<Integer, PlaceAndShapeObject> query = luceneService.createLuceneQueryFactory()
            .create(INDEX_NAME, region.getName(), key, "place");
        Collection<PlaceAndShapeObject> results = query.findValues();
        System.out.println("3. Given Coordinates are inside the shape :: " + results + "/n");
      }
    }
    CommonOps.closeCache();
  }

  private static Region<String, PlaceAndShapeObject> createClientRegion() {
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();
    return cache.<String, PlaceAndShapeObject>createClientRegionFactory(ClientRegionShortcut.PROXY)
        .create(REGION_NAME);

  }

  private static void putEntries(Map<String, PlaceAndShapeObject> region) {

    region.put("North Carolina", new PlaceAndShapeObject("North Carolina",
        " ((-23.543 -46.653 -23.5346 -46.634 -23.543 -46.613 -23.559 -46.614 -23.567 -46.631 -23.560 -46.653 -23.543 -46.653))"));
  }
}
