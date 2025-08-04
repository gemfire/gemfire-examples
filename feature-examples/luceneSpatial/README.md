<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->
<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# GemFire Lucene Spatial Indexing Example

These examples demonstrate how to use GemFire's LuceneSerializer and LuceneQueryProvider APIs
to customize how GemFire data is stored and indexed in Lucene.

All the examples are run from Example.java class's main method. In these example two servers host a partitioned region that stores the location information,including GPS coordinates. The region has lucene index that allows spatial queries to be performed against the data. These examples show how to do a spatial query.

First example is SearchNearestResultExample, it finds nearby locations from a specific location.

Second example is DistanceFacetsExample, it finds all the locations which coincides with the given location. 

Third example is SearchOverlappingLocation, it finds the location that overlaps with other locations.

Fourth example is SearchIntersectingCoordinates, it finds the location that coincides with a given shape (which consists of multiple locations). 

These example assumes that Java 11 and GemFire are installed. Minimum java version is jdk11. 

Note: These example use the GemFire Search extension which requires GemFire 10 to work

## Set up the Lucene index and region
1. Set directory ```gemfire-examples/luceneSpatial``` to be the current working directory.
Each step in this example specifies paths relative to that directory.

2. Build the examples

        $ ../gradlew build

3. Add Tanzu GemFire Search extension path to the `GEMFIRE_EXTENSIONS_REPOSITORY_PATH` environment
   variable. For example, if your vmware-gemfire-search-<version>.gfm file is located in
   /gemfire-extensions, use the following command:

        $ export GEMFIRE_EXTENSIONS_REPOSITORY_PATH=/gemfire-extensions

4. Run a script that starts a locator and two servers, creates a Lucene index called ```simpleIndex``` with a custom LuceneSerializer that indexes spatial data. The script
then creates the ```example-region``` region.

        $ gfsh run --file=scripts/start.gfsh

5. Run the examples to populate both the Lucene index and `example-region`. This program adds data to the example-region, and then performs the searches mentioned above.

        $ ../gradlew run


6. Shut down the cluster

        $ gfsh run --file=scripts/stop.gfsh

7. Clean up any generated directories and files so this example can be rerun.
    
        $ ../gradlew cleanServer

