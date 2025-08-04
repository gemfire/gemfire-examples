<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->

# Quickstart Tutorial - Introduction To Tanzu GemFire

Tanzu GemFire is an in-memory distributed Key-Value datastore.  As a datastore, GemFire provides a real-time, consistent and distributed service for modern applications with data-intensive needs and low latency response requirements. Because of GemFire's distributed peer-to-peer nature it can take advantage of multiples servers to pool memory, cpu and disk storage for improved performance, scalability and fault tolerance to build applications needing caching, management of in-flight data or the key-value database of record.

## Goal

The goal of this quickstart tutorial is to introduce GemFire basics, including starting a GemFire development environment, building and connecting a Java based GemFire client application to a cluster and performing basic CRUD operations.

## Download Examples and Configure Environment

Download and install Tanzu GemFire from [Tanzu Network](https://network.tanzu.vmware.com). Follow the installation instructions in the [GemFire documentation](https://docs.vmware.com/en/VMware-GemFire/index.html).

Clone the GemFire examples repository from GitHub.

```text
$ git clone git@github.com:gemfire/gemfire-examples.git
```

Set GEMFIRE_HOME environment variable to top of GemFire install directory. Note for this example Gemfire is installed in the home directory of the user - adjust as necessary for local environment and install directory location.

```text
$ export GEMFIRE_HOME=${HOME}/gemfire
```

Configure PATH to GemFire bin directory for access to gfsh utility.

```text
$ export PATH=${PATH}:${GEMFIRE_HOME}/bin
```

Validate Java 11 and Maven install.

```text
$ java -version

openjdk version "11.0.17" 2022-10-18
OpenJDK Runtime Environment (build 11.0.17+8-post-Ubuntu-1ubuntu2)
OpenJDK 64-Bit Server VM (build 11.0.17+8-post-Ubuntu-1ubuntu2, mixed mode, sharing)

$ mvn --version

Apache Maven 3.8.3
Maven home: /usr/share/maven
Java version: 11.0.17, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.19.0-29-generic", arch: "amd64", family: "unix"
```

In some environments it may be helpful to configure the JAVA_HOME environmental variable if issues are encountered or multiple Java versions are installed, along with setting a local PATH adding the JDK install directory.

## Configure Access to GemFire Maven Repository

The quickstart tutorial requires access to the Broadcom Maven Repository for the GemFire product jars. Please sign-up for access to repo at <https://support.broadcom.com/>.

Once sign-up is completed, add the following to the settings.xml file in .m2 directory within the home directory. Make sure to replace the email and
password with those used during sign-up.

```xml
<settings>
    <servers>
        <server>
            <id>gemfire-release-repo</id>
            <username>EXAMPLE-USERNAME@example.com</username>
            <password>EXAMPLE-PLAINTEXT-PASSWORD</password>
        </server>
    </servers>
</settings>
```

The pom.xml file provided with the examples is already configured with a pointer to the Tanzu GemFire maven repository and makes use of the GemFire 10.1.0 version of the product.

## What is a GemFire Cluster, Locators and Servers?

Tanzu GemFire is a distributed set of services which is generally referred to as a GemFire cluster. A GemFire cluster is made of at least one or more locators and at least one or more servers. Redundancy of services and data is achieved by running multiple locators and servers on multiple host machines. Locators provide discovery and management services for the cluster, servers provides data storage via regions, data distribution and compute services to the cluster.

## Start a Developer GemFire Cluster

For this tutorial we will will start a basic GemFire cluster for development with one locator and server.

Now start the development cluster with one locator and one server. This command will map the locator port to 10334 and the server port to 40404.  Keep this in mind as this is only a minimal cluster useful for local development. A production ready cluster configuration would typically have 2 locators and 3-5 servers to provide redundancy.

Start GemFire Locator on default port 10334, locator artifacts such as logs stored in ${HOME}/locator.

```text
$ gfsh start locator --name=locator --dir=${HOME}/locator

................
Locator in /home/<username>/locator on test-javaclient.localdomain[10334] as locator is currently online.
Process ID: 532579
Uptime: 10 seconds
GemFire Version: 10.1.0
Java Version: 11.0.17
Log File: /home/<username>/test/locator/locator.log
JVM Arguments: --add-exports=java.management/com.sun.jmx.remote.security=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /home/<username>/vmware-gemfire-10.1.0/lib/gemfire-core-10.1.0.jar:/home/<username>/vmware-gemfire-10.1.0/lib/gemfire-server-all-10.1.0.jar

Successfully connected to: JMX Manager [host=test-javaclient.localdomain, port=1099]

Cluster configuration service is up and running.

```

Start a GemFire Server with default cacheserver port of 40404.

```text
$ gfsh -e "connect" -e "start server --dir=${HOME}/server --name=server"

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=test-javaclient.localdomain, port=1099] ..
Successfully connected to: [host=test-javaclient.localdomain, port=1099]

You are connected to a cluster of version 10.1.0.


(2) Executing - start server --dir=test/server --name=server

...
Server in /home/<username>/server on test-javaclient.localdomain[40404] as server is currently online.
Process ID: 534075
Uptime: 3 seconds
GemFire Version: 10.1.0
Java Version: 11.0.17
Log File: /home/<username>/test/server/server.log
JVM Arguments: --add-exports=java.management/com.sun.jmx.remote.security=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -Dgemfire.default.locators=192.168.0.38[10334] -Dgemfire.start-dev-rest-api=false -Dgemfire.use-cluster-configuration=true -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /home/<username>/vmware-gemfire-10.1.0/lib/gemfire-core-10.1.0.jar:/home/<username>/vmware-gemfire-10.1.0/lib/gemfire-server-all-10.1.0.jar

```

A minimal GemFire cluster should now be available to use.

## Introduction to GFSH

Tanzu GemFire provides the command line tool "gfsh" for managing Gemfire clusters.  Gfsh can be used to start and stop members of the cluster along with configure additional features of the product.

Start interactive gfsh shell.

```text
$ gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /
/______/_/      /______/_/    /_/    10.1.0

Monitor and Manage Tanzu GemFire
gfsh>
```

Next use the *connect* command to access the default locator at localhost and port 10334 for performing management tasks on the running GemFire cluster.

```text
gfsh>connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=test-javaclient.localdomain, port=1099] ..
Successfully connected to: [host=test-javaclient.localdomain, port=1099]

You are connected to a cluster of version 10.1.0.

```

The above response is an affirmative connection to the locator at localhost and port 10334
 and should now be connected for doing management of the GemFire cluster.

Now use the gfsh interactive shell to issue a basic management command to list the members in the cluster.

```text
gfsh>list members

Member Count : 2

 Name   | Id
------- | -------------------------------------------------------------------
locator | test-javaclient(locator:532579:locator)<ec><v0>:41000 [Coordinator]
server | test-javaclient(server:534075)<v1>:41001

```

The above response of the "list members" command should show two members in the cluster.

There are multiple useful commands provided by gfsh to configure and manage GemFire clusters,
the *help* command will list them and the gfsh interactive shell has tab completion to help with determining options for each command. For additional information checkout the [GFSH documation](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/tools_modules-gfsh-chapter_overview.html) .

## Create a server Region

A [Region](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-region_options-chapter_overview.html) is the core structure in the server for holding and managing key-value data in GemFire, it is similar to a hashmap but is distributed across the server members.

While still in the gfsh interactive shell, create a [partition region](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-partitioned_regions-chapter_overview.html).

```text
gfsh>create region --name=petnames --type=PARTITION

Member  | Status | Message
------- | ------ | --------------------------------------
server | OK     | Region "/petnames" created on "server"

Cluster configuration for group 'cluster' is updated.
```

The region should now be created on server.

Time to exit the gfsh interactive shell, use the exit command.

```text
gfsh> exit
```

Response will be "Exiting..." and should now be back at the host terminal.

## Command Line Build

Use the following commands to build and run the client application at the terminal.

Build the client application with Maven and copy dependencies to target directory (note - commands should all be issued in the example directory that contains the pom.xml file).

```text
$ mvn clean compile dependency:copy-dependencies package
```

Set the classpath and run the client with the Java 11 virtual machine.

```text
$ java -cp target/GemFireClient-1.0-EXAMPLE.jar:target/dependency/*  com.vmware.gemfire.examples.quickstart.GemFireClient
```

## What's a GemFire Client?

The Tanzu GemFire client API is the primary way users, applications and microservices will access data, queries and the function service on the servers. A GemFire client connects to the cluster as an external process in a similar manner as classic client-server database model. The client application connects to the GemFire servers via the CacheServer port on the server to write and fetch data from the cluster using the GemFire Region API. The GemFire client is not a peer-peer member of the cluster and so may start or be stopped without effecting the membership of the cluster members or availability of data to other users.

## Client Configuration

The following "boiler plate" configuration code will exist in nearly all GemFire clients not using the Spring Framework.

### ClientCacheFactory and Cache

A client application will access and communicate with GemFire through ClientCache and Region instances.  The cache and region variables will be frequently referenced over the life cycle of an application.

```java
ClientCache cache;
Region<Integer,String> region;
```

Configure properties for the client cache, in this case setting logging to output to file client.log.

```java
Properties clientCacheProps=new Properties();
clientCacheProps.setProperty("log-level","config");
clientCacheProps.setProperty("log-file", "client.log");
```

Create client cache with properties and configure locators pool. The locator pool is a list of locators with ports used to provide discovery services for the client of available servers for fetching and storing data. The client will periodically check with the locators prior to making server connections, this is critical as cluster members can dynamically be added or removed.

```java
cache=new ClientCacheFactory(clientCacheProps).addPoolLocator("127.0.0.1", 10334)
    .create();
```

### Region

Regions are the prime way applications interact with data within GemFire. The client side region is a proxy to the region with the same name on the server and so local operations are sent to the server and the results returned to the client through the region API. In the following configuration the region is explicitly a proxy only and will not store any data in the local heap of the client. A region is similar to a hashmap in basic usage but with many additional features available.

```java
region=cache.<Integer,String>createClientRegionFactory(
ClientRegionShortcut.PROXY).create("petnames");
```

## Create Read Update Delete (CRUD)

At the core of any data driven application is the ability to do CRUD operations with supporting datastore.  All the basic CRUD operations are support are supported from client along with other advance options not covered in this tutorial. All the following code samples use the [Region API](https://gemfire.docs.pivotal.io/apidocs/tgf-915/org/apache/geode/cache/Region.html) and use a String key and value.

## Create data in GemFire

To create a new key-value on the server, the put method is used. In this case a key of 0 and a value of "Spot" will be put on the server region "example".

```java
region.put(0, "Spot");
```

## Read data from GemFire

To read data from the server, the client calls the get method with the key to be returned from the server. From the prior put operation the key-value of [0,"Spot"] exists on the server and so the get of key 0 will return value "Spot".

```java
returnedValue = region.get(0);
```

If a key-value pair doesn't exist on the server, a null value
will be returned to the client. As key 1 hasn't yet been put on the server, doing a get on this key will return a null.

```java
returnedValue = region.get(1); //null returned
```

## Update data in GemFire

Updating an existing key-value pair is similar to a create by using the put method to replace the value for the key. So below will change the value of key "A" on the server to now be 2.

```java
region.put(0, "Firehouse");
```

## Delete data in GemFire

Deleting data from the server uses the destroy method. The destroy for key 0 below will remove the key-value [0:"Firehouse"] from the server.

```java
client.region.destroy(0);
```

A key-value pair [0,"Firehouse"] has been destroyed, doing a get on the key will return a null value.

```java
returnedValue=region.get(0); //null returned
```

The client should output to stdout, the following messages once it runs with a little pre and post logging. There will also be a client.log file created with the client configuration and other useful logging for reviewing the runtime behavior.

```text
...
Put Key: 0 Value: Spot
Returned value: Spot with key: 0
Key-Value Not Found - Key: 1 Value: null
Update Key: 0 with new value: Firehouse
Returned value: Firehouse with key: 0
Removed Key-Value no longer found - Key: 0 Value: null
...
```

## Clean up

Once finished with the tutorial, one can stop the GemFire server and locator using gfsh.

```text
$ gfsh -e "connect" -e "shutdown --include-locators=true"

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=test-javaclient.localdomain, port=1099] ..
Successfully connected to: [host=test-javaclient.localdomain, port=1099]

You are connected to a cluster of version 10.1.0.


(2) Executing - shutdown --include-locators=true

Shutdown is triggered

```

The server and locator can be restarted from a stopped state using the prior gfsh commands.

```text
$ gfsh start locator --name=locator --dir=${HOME}/locator

$ gfsh -e "connect" -e "start server --dir=${HOME}/server --name=server"
```

Once finished with development the locator and server artifacts can be cleaned up by deleting the directories for the locator and server if the processes are stopped.

To clean-up build artifacts use the following Maven command from the examples directory.

```text
mvn clean
```

## Troubleshooting

### NoAvailableLocatorsException

If the following client error occurs:

```java
Exception in thread "main" org.apache.geode.cache.client.NoAvailableLocatorsException: Unable to connect to any locators in the list [/127.0.0.1:10334]
```

Check that the Locator is started and that it is correctly configured. Try connecting to the locator with gfsh to confirm it is running and or use jps to check
check if Locator process is running.

### NoAvailableServersException

If the following client error occurs:

```java
Exception in thread "main" org.apache.geode.cache.client.NoAvailableServersException: No servers found
```

Check that the Server is started and that it is correctly configured. Connect to locator with gfsh and do a list members to confirm if server is running.

### ServerOperationException or RegionDestroyedException

If the following client error occurs doing a cache operation such as a put or get:


```java
Exception in thread "main" org.apache.geode.cache.client.ServerOperationException: remote server on test-javaclient(95592:loner):57692:9d6a4229: : While performing a remote put
	at org.apache.geode.cache.client.internal.PutOp$PutOpImpl.processAck(PutOp.java:372)
	at org.apache.geode.cache.client.internal.PutOp$PutOpImpl.processResponse(PutOp.java:274)
...
	at org.apache.geode.internal.cache.AbstractRegion.put(AbstractRegion.java:445)
	at com.vmware.gemfire.examples.quickstart.GemFireClient.main(GemFireClient.java:31)

Caused by: org.apache.geode.cache.RegionDestroyedException: Server connection from [identity(192.168.0.38(95592:loner):57692:9d6a4229,connection=1; port=57696]: Region named /petnames was not found during put request
	at gemfire//org.apache.geode.internal.cache.tier.sockets.BaseCommand.writeRegionDestroyedEx(BaseCommand.java:629)
...
lambda$newThread$0(LoggingThreadFactory.java:120)
	at java.base/java.lang.Thread.run(Thread.java:829)
```

Check that the Region has been created and exists on the servers ( use gfsh list regions ).
