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
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;

import java.util.ArrayList;
import java.util.List;

public class GeoLocationSearchWithinAnAreaExample {
  public static void main(String[] args) throws InterruptedException, LuceneQueryException {
    Region<String, RegionInfo> region =
        ExampleCommon.createRegion("example-region-is-location-inside-shape");
    LuceneService luceneService = ExampleCommon.luceneService();
    // Add some entries into the region
    ExampleCommon.putEntries(luceneService, region);
    double givenLongitude = -46.653;
    double givenLatitude = -23.543;
    verifyIfGivenLocationIsInsideShape(region, givenLongitude, givenLatitude);
    ExampleCommon.closeCache();
  }


  public static void verifyIfGivenLocationIsInsideShape(Region<String, RegionInfo> region,
      double givenLongitude, double givenLatitude) {
    List<String> LatLongList = ExampleCommon.getLatLongList(region);
    List<Double> longitudeList = new ArrayList<>();
    List<Double> latitudeList = new ArrayList<>();
    for (String s : LatLongList) {
      longitudeList.add(region.get(s).getLongitude());
      latitudeList.add(region.get(s).getLatitude());
    }
    System.out.println("Given Coordinates are inside the shape : " + SpatialHelper
        .verifyLocationIsInsideShape(longitudeList, latitudeList, givenLongitude, givenLatitude));

  }
}
