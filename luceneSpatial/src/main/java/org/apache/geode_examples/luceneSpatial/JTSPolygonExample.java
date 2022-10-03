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
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

/*
 * The example shows if one place resides in the other place
 */

public class JTSPolygonExample {
  private static final String REGION = "example-region2";
  private static final String INDEX = "simpleIndex2";


  public static void verifyIfTheShapeIsPolygon() throws LuceneQueryException, InterruptedException {
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


    // find the shape of the provided coordinates
    String shape = StringUtils.substringBefore(SpatialHelper.getPolygonQuery().toString(), " ");
    LuceneQuery<String, PlaceAndShapeObject> query =
        luceneService.createLuceneQueryFactory().create(INDEX, region.getName(), shape, "shape");

    List results = query.findResults();
    System.out.println("4. Shape of the coordinates is : " + results + "/n");
    // Close the cache
    CommonOps.closeCache();

  }


  private static void putEntries(Region<String, PlaceAndShapeObject> region) {
    region.put("North Carolina", new PlaceAndShapeObject("North Carolina", "POLYGON"));
    region.put("California", new PlaceAndShapeObject("California", "POLYGON"));
    region.put("Utah", new PlaceAndShapeObject("Utah", "Rectangle"));
    region.put("Wyoming", new PlaceAndShapeObject("Wyoming", "Rectangle"));
  }
}
