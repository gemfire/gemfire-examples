<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
  -->

# GemFire Locator Launcher Example

This example demonstrates how to start a GemFire locator embedded in your own Java application
via the LocatorLauncher utilizing the GemFire Bootstrap. Using the GemFire Bootstrap is necessary to
load GemFire Extensions, like GemFire Search, and isolate GemFire from other loaded application
classes.

The class `com.vmware.gemfire.examples.launchers.ExampleLocatorApplication` has a `static` 
`void main(String[])` method, like any other Java application. It uses the 
`org.apache.geode.distributed.LocatorLauncher` to embed a GemFire locator in the Java application.

The Bash script `scripts/start.sh` executes through GemFire Bootstrap's 
`com.vmware.gemfire.bootstrap.Main` Java application class. The last command in the script includes
only the GemFire Bootstrap jar in the Java classpath. Your application classes and jars can be added
via the `--automatic-module-classpath` argument. You should not include any other jars in the Java 
classpath directly.

```shell
$ java -classpath gemfire-bootstrap.jar com.vmware.gemfire.bootstrap.Main \
    <application class> \
    --automatic-module-classpath <classes:jar:...> \
    [application arguments ...]
```

## Steps

1. From the `gemfire-examples/feature-examples/launchers/locator` directory, build the example.

        $ ../../gradlew build

2. Use the provided Bash shell script to execute the example application.

        $ scripts/start.sh

## Additional Information

The Bash script `scripts/start-gemfire-9.sh` is an example of how you would have run this same
application with GemFire 9 for comparison and upgrade examples. Notice the use of the
`geode-dependencies.jar` along with your application classes on the Java classpath.

```shell
$ java -classpath geode-dependencies.jar:[classes:jars:...] \ 
    <application class> \
    [application arguments ...]
```
