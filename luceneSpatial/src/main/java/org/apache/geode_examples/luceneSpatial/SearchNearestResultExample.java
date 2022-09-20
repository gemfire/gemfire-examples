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

public class SearchNearestResultExample {
  public static void main(String[] args) throws InterruptedException, LuceneQueryException {
    // connect to the locator using default port 10334
    Region<String, RegionInfo> region = ExampleCommon.createRegion("example-region");
    LuceneService luceneService = ExampleCommon.luceneService();
    // Add some entries into the region
    ExampleCommon.putEntries(luceneService, region);
    findNearbyMcDonalds(luceneService, region);
    ExampleCommon.closeCache();
  }

  public static void findNearbyMcDonalds(LuceneService luceneService, Region region)
      throws LuceneQueryException {
    LuceneQuery<Integer, RegionInfo> query = luceneService.createLuceneQueryFactory().create(
        "simpleIndex", region.getName(), index -> SpatialHelper.findWithin(-46.653, -23.543, 0.25));

    Collection<RegionInfo> results = query.findValues();
    System.out.println("Found stops: " + results);
  }

}
