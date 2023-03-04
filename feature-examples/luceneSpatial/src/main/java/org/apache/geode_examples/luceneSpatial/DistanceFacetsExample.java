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

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;

/*
 * The example shows how to do a spatial query to find all the locations which intersects given
 * location.
 */

public class DistanceFacetsExample {

  private static final String REGION = "locationRegion";
  private static final String INDEX = "simpleIndex";
  private static final double LONGITUDE = -46.653;
  private static final double LATITUDE = -23.543;

  public static void findDistance() throws LuceneQueryException, InterruptedException {

    Region region = CommonOps.createClientRegion(REGION);
    LuceneService luceneService = CommonOps.luceneService();
    CommonOps.putEntries(region);
    luceneService.waitUntilFlushed(INDEX, region.toString(), 1, TimeUnit.MINUTES);

    LuceneQuery<Integer, LocationObject> query =
        luceneService.createLuceneQueryFactory().create(INDEX, region.getName(),
            index -> SpatialHelper.findDistanceForTheGivenCoord(LONGITUDE, LATITUDE, 1000.25));
    Collection<LocationObject> results = query.findValues();
    System.out.println("2. Found intersecting location for given location: " + results);
    CommonOps.closeCache();
  }

}
