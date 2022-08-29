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
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;

public class JTSPolygonExample {
  public static void main(String[] args) throws InterruptedException, LuceneQueryException {

    Region<String, RegionInfo> region = ExampleCommon.createRegion("example-region-make-shape");
    LuceneService luceneService = ExampleCommon.luceneService(region);
    // Add some entries into the region
    ExampleCommon.putEntries(luceneService, region);
    makeAShape(region);
    ExampleCommon.closeCache();
  }

  public static void makeAShape(Region<String, RegionInfo> region) {
    Set<String> keySet = region.keySetOnServer();
    List<String> list = new ArrayList<String>(keySet);
    Collections.sort(list);
    List<Double> longitudeList = new ArrayList<>();
    List<Double> latitudeList = new ArrayList<>();
    for (String s : list) {
      longitudeList.add(region.get(s).getLongitude());
      latitudeList.add(region.get(s).getLatitude());
    }
    System.out.println("Shape of the provided coordinates is: "
        + SpatialHelper.getAShapeFromCoordinates(longitudeList, latitudeList));

  }
}
