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
package com.vmware.gemfire.examples.luceneSpatial;

import java.io.IOException;

import org.apache.geode.cache.lucene.LuceneQueryException;

public class Example {
  public static void main(String[] args)
      throws InterruptedException, LuceneQueryException, IOException {
    // find nearby McDonalds location
    SearchNearestResultExample.findNearbyMcDonalds();
    // find all the locations which intersects given location.
    DistanceFacetsExample.findDistance();
    // find the location that overlaps with other location
    SearchOverlappingLocation.findLocationWithinRectangle();
    // find Location that intersects with a shape
    SearchIntersectingCoordinates.findIntersectingLocationWithARectangle();

  }
}
