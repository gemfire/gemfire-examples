<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->

# Quickstart Tutorial -002- Introduction To User Objects and PDX

Tanzu GemFire is an in-memory distributed Key-Value datastore.  As a datastore, GemFire provides a real-time, consistent and distributed service for modern applications with data-intensive needs and low latency response requirements. Because of GemFire's distributed peer-to-peer nature it can take advantage of multiples servers to pool memory, cpu and disk storage for improved performance, scalability and fault tolerance to build applications needing caching, management of in-flight data or the key-value database of record.

## Goal

The goal of this quickstart tutorial is to introduce building and using user created objects with GemFire along with the GemFire Portable Data Exchange(PDX) framework for serialization.

## Prerequisite Required Software

* Tanzu GemFire 9.15.0 or later
* Apache Maven
* Java Developer Kit (JDK) 11
* (optional) Integrated Development Environment (IDE) such as Microsoft Visual Studio Code (vscode) or JetBrains IntelliJ IDEA

## Download Examples and Configure Environment

Download and install Tanzu GemFire from [Tanzu Network](https://network.tanzu.vmware.com). Follow the installation instructions in the [GemFire documentation](https://docs.vmware.com/en/VMware-GemFire/index.html).

Clone the GemFire examples repository from GitHub.

```text
$ git clone git@github.com:gemfire/gemfire-examples.git
```

Set Gemfire home environment variable to top of GemFire install directory. Note for this example Gemfire is installed in the home directory of the user - adjust as necessary for local environment and install directory location.

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

Start a GemFire cluster with a locator, configure PDX and start a server.

```text
$ gfsh start locator --dir=/home/${USERNAME}/locator --name=locator

$ gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"

$ gfsh -e "connect" -e "start server --name=server1 --dir=/home/${USERNAME}/server1"

```

Prior to the server start-up PDX serialization has to be enabled. The above configure command, sets PDX to enabled with readserializable set to true and disk persistence of the type registry. By setting readserializable to true the values on the server will be kept in PDX serialized format and accessed as PDX Instances.

## Create Region with GFSH

As a best practice each region should only contain a single type of instance data. Hence make sure to create a new region for each kind of data expected to be stored in GemFire and don't mix key types or values into an existing region with different data.

Create a "pets" region to hold the example data using gfsh.

```text
$ gfsh -e "connect" -e "create region --name=pets --type=PARTITION_PERSISTENT"

Member  | Status | Message
------- | ------ | -----------------------------------
server1 | OK     | Region "/pets" created on "server1"

Cluster configuration for group 'cluster' is updated.

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

## What is Serialization?

Serialization is a critical part of working with distributed and networked processes,  it is the task of converting data or object instances into formatted bytes that can be transmitted over the network or written to disk for storage. When networked process receives the formatted bytes, the process can rehydrate or deserialize the bytes back into the object instance in the process space.

## What is PDX?

GemFire Portable Data Exchange (PDX) is the preferred option for doing data serialization of values within GemFire. PDX provides support for cross language (C++, Java, C#) serialization and deserialization using a compact format. PDX stores the serialized data in named fields that can be directly accessed avoiding the cost of deserializing the entire data object. The preference to keep data in a serialized form helps to reduce GC and memory pressure within GemFire servers while still providing access via the PdxInstance type. Another advantage to using PDX is that it allows versioning of value objects thus permitting changes such as adding or removing fields over the development lifecycle of an application.

For addition information on PDX and how GemFire handles serialization [review the GemFire docs](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-data_serialization-chapter_overview.html).

## User Created Key and Value Instances

This example uses a domain class as the value called Pet. The Pet instances will be serialized to the server using PDX and will be accessed as PdxInstances on the server side. The key is an integer ID number which uses default Java serialization to send the key to the server. The preferred key type is to use an Integer or String especially if doing PDX serialization.

There are critical implementation features that domain classes must follow
for serialization and for GemFire to store and retrieve the instance data.

### Important Value Implementation Features

* The domain value class in this example will be serialized via PDX. Class fields that are either static or transient will not be serialized. The class must provide a zero argument constructor.

```java
public Pet() {
}
```

The class doesn't have to implement java.io.Serializable but also will not effect its usage if it does. Implementing hashCode and equals is recommended but is not used directly by PDX instances but may be useful once a PDX instance is converted back to its original type.

In this example the instance data will be serialized using the [ReflectionBasedAutoSerializer](https://gemfire.docs.pivotal.io/apidocs/tgf-915/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html) which is configured during cache creation. This serializer uses Java reflection to determine the fields of the class along with a pattern string to determine which class instances are to be serialized via PDX and optionally if any fields will be excluded from serialization.

For more complex situations GemFire provides extensive documentation on [PDX](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-data_serialization-gemfire_pdx_serialization.html) domain class implementation with custom serialization options.

## Client Application

A client application will access and communicate with GemFire through ClientCache and Region instances. The cache and region variables will be frequently referenced over the life cycle of an application. Configure properties for the client cache, in this case setting logging to output to file client.log.

```java
ClientCache cache;
Region<PetId, Pet> region;

Properties clientCacheProps = new Properties();
clientCacheProps.setProperty("log-level", "config");
clientCacheProps.setProperty("log-file", "client.log");
```

## Cache Creation and PDX Configuration

Create client cache with properties and configure locators pool. The locator pool is a list of locator IP's with ports used to provide discovery services for the client of available servers for fetching and storing data. The client will periodically check with the locators prior to making server connections, this is critical as cluster members can dynamically be added or removed.

The PDX serializer is configured to use the ReflectionBasedAutoSerializer and is configured to serialize the local Pet instances as a PDX instance when sent to the server. The reflection based PDX serializer avoids significant customization of the Pet class by only requiring a zero argument constructor. Classes not listed or captured with a wildcard pattern when configuring the reflection based serializer will use Java serialization instead of PDX and may require uploading class files to servers.  Only value classes should be included, do not include key classes when listing types to be serialized by the reflection based serializer.

```java
cache = new ClientCacheFactory(clientCacheProps)
    .addPoolLocator(
        "127.0.0.1", 10334)
    .setPdxSerializer(
        new ReflectionBasedAutoSerializer(
            true,
            "com.vmware.gemfire.examples.quickstart.Pet"
        )
    )
.create();
```

Create local region proxy with the same name as the one created on the server. Data operations performed on local region will be reflected on the on with the same name on the server.

```java
region = cache.<PetId, Pet>createClientRegionFactory(
    ClientRegionShortcut.PROXY).create("pets");
```

## CRUD Operations

Create local instance data with pet as the value.

```java
Pet pet = new Pet("Poodle", "Spot", "Bill");
```

Store/put data in the pets region - Creating a new key:value pair on the server.

```java
region.put(1,pet2);
```

Get the data back from the pets server region. Retreive the key:value pair.

```java
returnedPetValue = region.get(0);
System.out.println("Returned pet: "+returnedPetValue.toString());
```

Update the value data for a key by doing a put.

```java
pet.setOwner("Joyce");
region.put(0, pet);
returnedPetValue = region.get(0);
System.out.println("Returned pet: "+returnedPetValue.toString());
```

Delete the key and value.

```java
region.destroy(0);
System.out.println("Returned pet: "+region.get(0)); // null returned
```

The client should output to stdout, the following messages once it runs with a little pre and post logging. There will also be a client.log file created with the client configuration and other useful logging for reviewing the runtime behavior.

```text
...
Returned pet: Pet [breed=Poodle, name=Spot, owner=Bill]
Returned pet: Pet [breed=Poodle, name=Spot, owner=Joyce]
Returned pet: null
...
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

## Appendix - Domain/User Keys and Special Requirements

### Important Key Implementation Features

* The preferred key type whenever possible is to use an Integer or String especially if doing PDX serialization. The class type data will not be required on the servers when using an Integer or String as a key.  For illustrative purposes the PetId is a custom class and demonstrates all the requirements for usage with GemFire as a key but could easily be replaced with just an Integer for the ID number as a key.

* Key classes should use the java.io.Serializable interface which has no methods to implement but rather acts as a marker on the class and its data for serialization. There are contracts that need to be followed for its usage, see [javadocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Serializable.html) for java.io.Serializable.

```java
public class PetId implements java.io.Serializable
```

* The hashCode and equals methods must be overridden. The default hash code from Object is not stable across JVM instances and hence will produce hash codes that will not work correctly with GemFire. Hence it is critical to override the hashCode and that it uses values of the class that are stable across JVM instances. Examples of stable hash code generation includes Integer and String classes and make a good base for key classes.

The PetId class uses a String name and int idNum field as the key identity. The following hashcode method uses both these fields to create a stable hashcode.

```java
//IDE Generated Method
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + idNum;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
}
```

* The equals method is also critically import and works in conjunction with the hashCode method.

```java
//IDE Generated Method
@Override
public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    PetId other = (PetId) obj;
    if (idNum != other.idNum)
        return false;
    if (name == null) {
        if (other.name != null)
            return false;
    } else if (!name.equals(other.name))
        return false;
    return true;
}
```

**Best practice** - Most IDE's today can generate a stable hashCode and equals methods for users and generally only require review of the generated code for correctness.

For additional details see [javadocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/package-summary.html) for java.lang.Object and java.long.String.

For additional information and advanced usage of keys with PDX see the [GemFire docs](https://docs.vmware.com/en/VMware-GemFire/10.1/gf/developing-data_serialization-using_pdx_region_entry_keys.html).

## Upload Classes and Run Client

The PetId class needs to be available on the GemFire servers. Before running client, compile and package classes for upload to the servers. Use gfsh to upload the classes to the server.

```text
$ gfsh -e "connect" -e "deploy --jar target/GemFireClient-1.0-EXAMPLE.jar"
```

For convenience of the example, the jar file includes all the classes including those not required on the server. Only the PetID is required for this example. As a best practice only the critical classes required for serialization, callbacks or server operations should be packaged and uploaded to the servers. Value classes that are serialized via PDX and used as PdxInstances on the server side do not need to be uploaded to the servers. Integers, Strings and other base classes of the JDK also do not need to uploaded to the servers.

Update GemFireClient.java application by adding lines instantiating the PetId key and replace the integer key with an instance of the PetID.

Replace integer key similar to this code:

```java
region.put(0, pet);
```

With the PetId key :

```java
PetId petid=new PetId(0,"Spot");
region.put(petid, pet);
```

Along with updating additional lines where a key is required to get data.

```java
returnedPetValue = region.get(0);
```

Changing line to become:

```java
returnedPetValue = region.get(petid);
```
