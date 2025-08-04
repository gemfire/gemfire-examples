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

# GemFire WAN replication example

This example demonstrates GemFire support for asynchronous WAN 
replication between clusters.  WAN replication allows remote GemFire 
clusters to automatically keep their region data consistent through
the use of gateway senders and receivers. A gateway sender distributes 
region events to another, remote GemFire cluster. A gateway receiver 
configures a physical connection for receiving region events from 
gateway senders in remote GemFire clusters. The gateway senders and 
receivers can be configured in several different topologies based on 
specific business needs. For more information on example topologies 
and associated use cases see GemFire documentation on 
[Multi-site WAN Configuration](http://geode.apache.org/docs/guide/topologies_and_comm/multi_site_configuration/chapter_overview.html)

In this example, two clusters are created on your local machine, each
with a unique distributed system id and the WAN gateway configured
for active-active, bidirectional region updates. The New York cluster (ny) 
has id=1 and the London cluster (ln) has id=2. Each cluster contains the same 
partitioned region (example-region) and each has parallel gateway senders, 
which means each server in the cluster will send data updates for 
the primary region buckets they hold.  Alternately, you can configure 
serial gateway senders, where only one server in each cluster sends all data 
updates across the WAN. Serial gateway senders are typically used for 
replicated regions or when the order of events between different keys in
a partitioned region needs to be preserved.

This example runs a single client that connects to the London cluster and 
puts 10 entries into the example-region and prints them.  After the client
app has run, both clusters will contain the data.

This example assumes that JDK11 and GemFire are installed.

## Steps

1. From the `gemfire-examples/wan` directory, build the client app example 

        $ ../gradlew build

2. Run the script that starts the London and New York clusters.  Each cluster includes one locator
   and two servers.  Each server configures one gateway sender, one gateway receiver and one
   partitioned region attached to the gateway sender.

        $ gfsh run --file=scripts/start.gfsh

5. Run the client example app that connects to the London cluster and puts 10 entries 
into the `example-region`. The data will be automatically sent to the New York cluster,
as well as printed to the console.

        $ ../gradlew run

6. In one terminal, run a `gfsh` command, connect to the New York cluster, and verify
   the region contents

        $ gfsh
        ...
        Cluster-1 gfsh>connect --locator=localhost[10331]
        Cluster-1 gfsh>query --query="select e.key, e.value from /example-region.entries e"
        ...

7. In another terminal, run a `gfsh` command, connect to the London cluster, and verify
   the region contents

        $ gfsh
        ...
        Cluster-2 gfsh>connect --locator=localhost[10332]
        Cluster-2 gfsh>query --query="select e.key, e.value from /example-region.entries e"
        ...

8. Use other gfsh commands to learn statistics about the regions, gateway senders,
   and gateway receivers for each cluster.

        Cluster-1 gfsh>describe region --name=example-region
        Cluster-1 gfsh>list gateways

9. In the terminal connected to the New York cluster, put another entry in the region 
   and verify it is in the region on this cluster.

        Cluster-1 gfsh>put --key=20 --value="value20" --region=example-region
        Cluster-1 gfsh>query --query="select e.key, e.value from /example-region.entries e"

10. In the terminal connected to the London cluster, verify the new entry has also 
    been added to the region on this cluster.

        Cluster-2 gfsh>query --query="select e.key, e.value from /example-region.entries e"

11. Feed data into regions
        ../gradlew run

12. Do wan-copy region with wild card
    # Note: step 12 has to work with gemfire 10.2+.
        $ gfsh
        ...
        Cluster-2 gfsh>connect --locator=localhost[10332]

        # previous single region wan-copy region command is still supported
        Cluster-2 gfsh>wan-copy region --region=example-region --sender-id=ny

        # 2 colocated regions can use the same sender id
        Cluster-2 gfsh>wan-copy region --region=example-region2,example-region3 --sender-id=ny2

        # region name with wild card
        Cluster-2 gfsh>wan-copy region --region=example-region* --sender-id=ny*
        Cluster-2 gfsh>wan-copy region --region=example-region? --sender-id=ny*

        # all regions
        Cluster-2 gfsh>wan-copy region --region=* --sender-id=*

        # sender id becomes optional
        Cluster-2 gfsh>wan-copy region --region=example-region*

        # --simulate: list out all the region and sender id pairs, but not to do copy
        Cluster-2 gfsh>wan-copy region --region=example-region* --sender-id=ny* --simulate

        # --background: run wan-copy region operation in background and return the wan-copy-id immediately
        Cluster-2 gfsh>wan-copy region --region=example-region* --sender-id=ny* --background

        # list running and finished wan-copy region operations
        Cluster-2 gfsh>list wan-copy region

        # describe a wan-copy region operation via its id
        Cluster-2 gfsh>describe wan-copy region --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad

        # describe a wan-copy region operation in verbose format
        Cluster-2 gfsh>describe wan-copy region --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad --verbose

    While wan-copy is on-going, connect to Cluster-2 in another terminal and cancel the wan-copy
        $ gfsh
        ...
        Cluster-2 gfsh>connect --locator=localhost[10332]

        # Without --wan-copy-id parameter, only cancel the current running wan-copy region operation
        Cluster-2 gfsh>wan-copy region --region=example-region --sender-id=ny --cancel
        Cluster-2 gfsh>wan-copy region --region=example-region* --sender-id=ny* --cancel
        Cluster-2 gfsh>wan-copy region --region=* --sender-id=* --cancel

        # cancel with wan-copy-id
        # cancel one region senderid pair (example-region,ny) in the wan-copy-id
        Cluster-2 gfsh>wan-copy region --region=example-region --sender-id=ny --cancel --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad

        # cancel all combination of (example-region*,ny*) belong to that wan-copy-id, including unstarted wan-copy region operations
        Cluster-2 gfsh>wan-copy region --region=example-region* --sender-id=ny* --cancel --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad

        # cancel all wan-copy region operations belong to that wan-copy-id, including unstarted wan-copy region operations
        Cluster-2 gfsh>wan-copy region --region=* --sender-id=* --cancel --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad

        # cancel all wan-copy region operations belong to that wan-copy-id, including unstarted wan-copy region operations
        Cluster-2 gfsh>cancel wan-copy region --wan-copy-id=5d1e7efd-bd16-4fa7-b925-635c797ac9ad

        # After set up gemfire cluster with 10.2+. Edit scripts/stop.gfsh to uncomment the line "run --file=scripts/wan-copy.gfsh", or run "gfsh run --file=scripts/wan-copy.gfsh" in command line.

13. Exit gfsh in each terminal and shutdown the cluster using the stop.gfsh script
 
        $ gfsh run --file=scripts/stop.gfsh

14. Clean up any generated directories and files.

    	$ ../gradlew cleanServer

