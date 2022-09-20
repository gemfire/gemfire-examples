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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JTSPolygonExample {
  public static void main(String[] args) throws InterruptedException, LuceneQueryException {

    Region<String, RegionInfo> region = ExampleCommon.createRegion("example-region");
    LuceneService luceneService = ExampleCommon.luceneService();
    // Add some entries into the region
    ExampleCommon.putEntries(luceneService, region);
    getTheShapeOfTheCoordinates(region, luceneService);
    ExampleCommon.closeCache();
  }

  public static void getTheShapeOfTheCoordinates(Region<String, RegionInfo> region,
      LuceneService luceneService) throws LuceneQueryException {
    Set<String> keySet = region.keySetOnServer();
    List<String> list = new ArrayList<String>(keySet);
    Collections.sort(list);
    List<Double> longitudeList = new ArrayList<>();
    List<Double> latitudeList = new ArrayList<>();
    for (String s : list) {
      longitudeList.add(region.get(s).getLongitude());
      latitudeList.add(region.get(s).getLatitude());
    }

    LuceneQuery<String, RegionInfo> query =
        luceneService.createLuceneQueryFactory().create("simpleIndex", region.getName(),
            luceneIndex -> SpatialHelper.getTheShape(longitudeList, latitudeList));

    Collection<RegionInfo> results = query.findValues();
    System.out.println("Shape of the provided coordinates is: " + results);

  }
}
