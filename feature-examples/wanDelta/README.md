<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->

# GemFire WAN Delta replication example

This example demonstrates GemFire support for asynchronous WAN 
replication between clusters using Delta objects.  WAN replication allows
remote GemFire clusters to automatically keep their region data consistent
through the use of gateway senders and receivers. A gateway sender distributes 
region events to another, remote GemFire cluster. A gateway receiver 
configures a physical connection for receiving region events from 
gateway senders in remote GemFire clusters. The gateway senders and 
receivers can be configured in several different topologies based on 
specific business needs. For more information on example topologies 
and associated use cases see GemFire documentation on 
[Multi-site WAN Configuration](http://geode.apache.org/docs/guide/topologies_and_comm/multi_site_configuration/chapter_overview.html) 
and [WAN Delta](http://geode.apache.org/docs/guide/topologies_and_comm/multi_site_configuration/chapter_overview.html).

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
puts Delta entries into the example-region.  Each Delta object is initialized 
with a 10k byte payload.  Each create event will cause the entire Delta 
object to be replicated across the WAN.  The client will then update each 
Delta object with a small change.  Each Delta update event will only cause 
the changes to be replicated across the WAN.  After the client app has run, 
both clusters will contain the data.

This example assumes that JDK11 and GemFire are installed.

## Steps

1. From the `gemfire-examples/wan-delta` directory, build the client app example 

        $ ../gradlew build

2. Run the script that starts the London and New York clusters.  Each cluster includes one locator
   and two servers.  Each server configures one gateway sender, one gateway receiver and one
   partitioned region attached to the gateway sender.

        $ gfsh run --file=scripts/start.gfsh

6. Run the client example app that connects to the London cluster and puts Delta entries 
   into the `example-region` and then updates them. The data will be automatically sent to
   the New York cluster, as well as printed to the console.

        $ ../gradlew run

   For each entry, the London and New York server logs will show messages like below. 
   The `fromData` call means the full SimpleDelta object was received and deserialized.
   Each `fromDelta` call means only the update was received and applied.

   ```
   [info ...] SimpleDelta.fromData invoked on SimpleDelta[id=10; version=0; payloadLength=10240]
   
   [info ...] SimpleDelta.fromDelta invoked on SimpleDelta[id=10; version=1; payloadLength=10240]
   
   [info ...] SimpleDelta.fromDelta invoked on SimpleDelta[id=10; version=2; payloadLength=10240]
   ```

7. In one terminal, run a `gfsh` command, connect to the New York cluster, and verify
   the region contents

        $ gfsh
        ...
        Cluster-1 gfsh>connect --locator=localhost[10331]
        Cluster-1 gfsh>query --query="select e.key, e.value.toString from /example-region.entries e"
        ...

8. In another terminal, run a `gfsh` command, connect to the London cluster, and verify
   the region contents

        $ gfsh
        ...
        Cluster-2 gfsh>connect --locator=localhost[10332]
        Cluster-2 gfsh>query --query="select e.key, e.value.toString from /example-region.entries e"
        ...

9. Use other gfsh commands to learn statistics about the regions, gateway senders,
    and gateway receivers for each cluster.

         Cluster-1 gfsh>describe region --name=example-region
         Cluster-1 gfsh>list gateways

10. Exit gfsh in each terminal and shutdown the cluster using the stop.gfsh script
 
         $ gfsh run --file=scripts/stop.gfsh

11. Clean up any generated directories and files.

        $ ../gradlew cleanServer

