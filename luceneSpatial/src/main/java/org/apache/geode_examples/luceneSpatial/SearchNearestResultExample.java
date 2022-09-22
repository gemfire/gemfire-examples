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
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;

import java.util.Collection;

/*
 * The example shows how to do a spatial query to find nearby McDonalds location.
 */

public class SearchNearestResultExample {
  public static void main(String[] args) throws InterruptedException, LuceneQueryException {
    // Create client region which is same as the region on the server
    Region<String, LocationInfo> region = CommonOps.createClientRegion("example-region");
    // Create Lucene Service
    LuceneService luceneService = CommonOps.luceneService();
    // Add some entries into the region
    CommonOps.putEntries(luceneService, region);
    // Search the desired location
    findNearbyMcDonalds(luceneService, region);
    // Close the cache
    CommonOps.closeCache();
  }

  public static void findNearbyMcDonalds(LuceneService luceneService, Region region)
      throws LuceneQueryException {
    LuceneQuery<Integer, LocationInfo> query = luceneService.createLuceneQueryFactory().create(
        "simpleIndex", region.getName(), index -> SpatialHelper.findWithin(-46.653, -23.543, 0.25));

    Collection<LocationInfo> results = query.findValues();
    System.out.println("Found stops: " + results);
  }

}
