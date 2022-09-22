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

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneService;

/*
 * The example shows the distance between two locations
 */
public class DistanceFacetsExample {
  public static void main(String[] args) throws InterruptedException {
    // Create client region which is same as the region on the server
    Region<String, LocationInfo> region = CommonOps.createClientRegion("example-region");
    // Create Lucene Service
    LuceneService luceneService = CommonOps.luceneService();
    // Add some entries into the region
    CommonOps.putEntries(luceneService, region);
    // Given location
    double sourceLat = 36.8738;
    double sourceLong = -78.78412;
    // Find distance between two locations
    findDistance(region, sourceLat, sourceLong);
    // Close the cache
    CommonOps.closeCache();
  }

  public static void findDistance(Region<String, LocationInfo> region, double sourceLat,
      double sourceLong) {
    Set<String> keySet = region.keySetOnServer();
    for (String s : keySet) {
      double distance = SpatialHelper.getDistanceInMilesFromTwoLocations(sourceLat, sourceLong,
          region.get(s).getLatitude(), region.get(s).getLongitude());
      System.out.println("Distance between the source and destination is : " + distance);
    }
  }
}
