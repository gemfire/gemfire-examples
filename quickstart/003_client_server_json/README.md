<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->

# Quickstart Tutorial -003- Introduction To JSON Document Storage

Tanzu GemFire is an in-memory distributed Key-Value datastore.  As a datastore, GemFire provides a real-time, consistent and distributed service for modern applications with data-intensive needs and low latency response requirements. Because of GemFire's distributed peer-to-peer nature it can take advantage of multiples servers to pool memory, cpu and disk storage for improved performance, scalability and fault tolerance to build applications needing caching, management of in-flight data or the key-value database of record.

## Goal

The goal of this quickstart tutorial is to introduce using and storing JSON documents in GemFire, in particular using the newly introduced in GemFire 10 the JsonDocumentFactory and JsonDocument classes.

## Prerequisite Required Software

* Tanzu GemFire 10.0 or later
* Apache Maven
* Java Developer Kit (JDK) 11
* (optional) Integrated Development Environment (IDE) such as Microsoft Visual Studio Code (vscode) or JetBrains IntelliJ IDEA

## Download Examples and Configure Environment

Download and install Tanzu GemFire from [Tanzu Network](https://network.tanzu.vmware.com). Follow the installation instructions in the [GemFire documentation](https://docs.vmware.com/en/VMware-GemFire/index.html).

Clone the GemFire examples repository from GitHub.

```text
$ git clone git@github.com:gemfire/gemfire-examples.git
```

Set Gemfire home environment variable to top of GemFire install directory. Note for this example Gemfire is installed in the home directory of the user - adjust as nessagery for local environment and install directory location.

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

## Start a Developer GemFire Cluster

Start a GemFire cluster with a locator, configured PDX and a server.

```text
$ gfsh start locator --dir=/home/${USERNAME}/locator --name=locator

$ gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"

$ gfsh -e "connect" -e "start server --name=server1 --dir=/home/${USERNAME}/server1"

```

## Create Region with GFSH

As a best practice each region should only contain a single type of instance data. Hence make sure to create a new region for each kind of data expected to be stored in GemFire and don't mix key types or values into an existing region with different data.

Create a "petrecords" region to hold the example data using gfsh.

```text
$ gfsh -e "connect" -e "create region --name=petrecords --type=PARTITION_PERSISTENT"
```

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

## Managing JSON Documents?

Starting in GemFire 10.0 and later, a new API for managing JSON documents is provided. JSON documents are converted to a JsonDocument type for serialization to the GemFire servers. The JsonDocument instance is an immutable type, that implements the org.apache.geode.cache.Document interface. A default JsonDocument uses the BSON format to internally store the JSON data. JsonDocument's support server side operations such as query without conversion to another type.

To create a JsonDocument, use an instance of JsonDocumentFactory obtained from RegionService and use create(String json) method to parse the JSON string returning a JsonDocument instance.

A JsonDocument can be converted back to a JSON string with the toJson() method. Individual fields of original JSON can accessed via the getField(String fieldName). The getField(String) method returns a Java object, the following table shows the mapping from JSON field to Java type. For example calling getField(String) for data that was a JSON array would return a Java List instance.

 JSON | Java |
| ----------- | ----------- |
| Object | JsonDocument |
| Array | List |
| String | String |
| "true" | Boolean.TRUE |
| "false" | Boolean.FALSE |
| "null" | null |
| Number | Integer, Long, BigInteger, or Double |

For additional details on using JSON with GemFire see [documentation](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-data_serialization-jsonformatter_pdxinstances.html)

For class details see JavaDocs for [JsonDocument](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/json/JsonDocument.html) and
[JsonDocumentFactory](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/json/JsonDocumentFactory.html).

See [BSON](https://bsonspec.org) and [JSON](https://www.json.org/json-en.html) specifications for additional information on JSON.

## Client Application

A client application will access and communicate with GemFire through ClientCache and Region instances.

```java
ClientCache cache;
Region<Integer, JsonDocument> region;

Properties clientCacheProps = new Properties();
clientCacheProps.setProperty("log-level", "config");
clientCacheProps.setProperty("log-file", "client.log");
```

## Cache Creation and Region

Create client cache with properties and configure locators pool.

```java
cache = new ClientCacheFactory(clientCacheProps)
    .addPoolLocator("127.0.0.1", 10334).create();
```

Create a region proxy "petrecords" matching one created on the server.

```java
region = cache.<Integer, JsonDocument>createClientRegionFactory(
        ClientRegionShortcut.PROXY).create("petrecords");
```

## JSON Document Storage

Create a String from a JSON document.
```java
String jsonPetRecord = "{" +
    " petname:\"Spot\"," +
    " idNum:0," +
    " breed:\"Poodle\"," +
    " owner:\"Bill\"," +
    " currentOnVaccines:true," +
    " issues:[\"needs special diet\",\"pulls on leash when walked\"]" +
"}";
```

Get the default JsonDocumentFactory from the Cache (RegionService).
```java
JsonDocumentFactory jdf = cache.getJsonDocumentFactory();
```

Use JsonDocumentFactory to convert JSON to JsonDocument instances.

```java
try {
    petRecord = jdf.create(jsonPetRecord);
} catch (JsonParseException e) {
    e.printStackTrace();
}
```

Put JSON document into GemFire servers.
```java
region.put(0, petRecord);
```

Get key 1 from servers returning a JsonDocument. Output JSON.

```java
pt = region.get(1);
System.out.println("JSON Pet Record: " + pt.toJson());
```

List the field names of the JsonDocument.

```java
List list = (List) pt.getFieldNames();
System.out.println("JSON Fields: " + list.toString());
```

Fetch field "petname" from JsonDocument.

```java
System.out.println("PetName Field: " + pt.getField("petname"));
```

Fetch the Array field "issues" and display the first element.

```java
System.out.println("Issues Field: "
    + ((List) (pt.getField("issues"))).get(0));
```

Compare two JSON documents.

```java
if (pt.equals(petRecord2)) {
    System.out.println("Documents are equal");
}
```

The client should output to stdout, the following messages once it runs with a little pre and post logging. There will also be a client.log file created with the client configuration and other useful logging for reviewing the runtime behavior.

```text
JSON Pet Record: {"petname":"Firehouse","idNum":1,"breed":"Dalmatian","currentOnVaccines":true,"owner":"Joe","issues":["bites mail person"]}
JSON Fields: [petname, idNum, breed, currentOnVaccines, owner, issues]
PetName Field: Firehouse
Issues Field: bites mail person
Documents are equal
```

## Close Client

Prior to exiting client close local GemFire cache and connection pool.

```java
System.out.println("Closing Client");
cache.close();
```

## Cleanup GemFire Processes

Shutdown the GemFire Locator and Server.

```text
$ gfsh -e "connect" -e "shutdown --include-locators=true"
```
