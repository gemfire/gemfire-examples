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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

// This example verifies if the given area of a place is within another area of the place
public class BoundingAreaExample {
  private static final String REGION = "example-region2";
  private static final String INDEX = "simpleIndex2";



  // Add some entries into the region


  public static void VerifyIfOneAreaIsWithinAnotherArea()
      throws LuceneQueryException, InterruptedException {

    // Create client region which is same as the region on the server

    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();
    Region<String, PlaceAndShapeObject> region =
        cache.<String, PlaceAndShapeObject>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create(REGION);

    // Create Lucene Service
    LuceneService luceneService = LuceneServiceProvider.get(cache);
    // Add some entries into the region
    putEntries(region);

    luceneService.waitUntilFlushed(INDEX, region.getName(), 1, TimeUnit.MINUTES);


    double areaPlaceA = getArea(region, "PlaceA");
    double areaPlaceB = getArea(region, "PlaceB");
    if (areaPlaceB > areaPlaceA) {
      LuceneQuery<Integer, PlaceAndShapeObject> query = luceneService.createLuceneQueryFactory()
          .create(INDEX, region.getName(), "PlaceA", "place");
      List<LuceneResultStruct<Integer, PlaceAndShapeObject>> results = query.findResults();
      System.out.println("5. Place B is inside :" + results + "/n");
    }
  }

  private static double getArea(Region<String, PlaceAndShapeObject> region, String key) {
    String coordinates = (StringUtils.substringBetween(region.get(key).getShape(), "((", "))"));
    String[] coordinatesArray = coordinates.split(" ");

    return SpatialHelper.computeArea(coordinatesArray);
  }

  private static void putEntries(Region<String, PlaceAndShapeObject> region) {
    region.put("PlaceA", new PlaceAndShapeObject("PlaceA",
        "((-23.543 -46.653 -23.5346 -46.634 -23.543 -46.613 -23.559 -46.614 -23.567 -46.631 -23.560 -46.653 -23.543 -46.653))"));
    region.put("PlaceB", new PlaceAndShapeObject("PlaceB",
        "((-18.543 -30.653 -18.5346 -30.634 -18.543 -30.613 -18.559 -30.614 -18.567 -30.631 -18.560 -30.653 -18.543 -30.653))"));
  }

}
