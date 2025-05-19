<!--
  ~ Copyright (c) VMware, Inc. 2025. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
  -->

# Transaction operations mixed with non-transactional operations

This example shows data inconsistency that happens when transactions are mixed with non-transactional operations

There are two packages in this example
* `com.vmware.gemfire.examples.transactionsMixedWithNon.reproduce` - an example that reproduces the problem
* `com.vmware.gemfire.examples.transactionsMixedWithNon.repair` - Utility functions to find inconsistent data in an existing cluster and remove inconsistent entries from all members.

## Utility functions
### `FindOldEntriesFunction` 
This function finds all entries with a last modified time older than a specified age. It checks each of these entries to see if the entry exists on the primary

To use this function, invoke it from gfsh. The first argument is the region. The second
argument is a number of minutes. Only entries older than this age will be examined.
```shell
execute function --id=FindOldEntriesFunction --arguments=example-region,60
```

## Reproduction example

The example runs for 60 seconds. A client does two different operations in different threads
* One thread performs transactional putAlls
* A second thread invokes a server side function that queries for entries, updates the entries, and removes them, without using a transaction

### Reproduction Steps

1. Build the code

        $ ./gradlew build

2. Next start a locator, start three servers, create a region, and deploy the function.

        $ gfsh run --file=scripts/start.gfsh

3. Run the example 

        $ ./gradlew run

4. Data inconsistency should be reported

```
        $ > Task :transactionsMixedWithNon:run
Exception in thread "main" org.apache.geode.cache.execute.FunctionException: java.lang.RuntimeException: Data does not match for bucket 110. Primary is RVK6VVPKHF(server2:85228)<v2>:53458
Inconsistency in key 1622741977|-1160214649. Value in RVK6VVPKHF(server1:85214)<v1>:55157 is UNPROCESSED. Value in RVK6VVPKHF(server2:85228)<v2>:53458 is null

        at gemfire//org.apache.geode.internal.cache.tier.sockets.command.ExecuteFunction70.cmdExecute(ExecuteFunction70.java:271)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.BaseCommand.execute(BaseCommand.java:193)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.ServerConnection.doNormalMessage(ServerConnection.java:901)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.ServerConnection.doOneMessage(ServerConnection.java:1113)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.ServerConnection.run(ServerConnection.java:1394)
        at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
        at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.AcceptorImpl.lambda$initializeServerConnectionThreadPool$3(AcceptorImpl.java:710)
        at gemfire//org.apache.geode.logging.internal.executors.LoggingThreadFactory.lambda$newThread$0(LoggingThreadFactory.java:124)
        at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: java.lang.RuntimeException: Data does not match for bucket 110. Primary is RVK6VVPKHF(server2:85228)<v2>:53458
Inconsistency in key 1622741977|-1160214649. Value in RVK6VVPKHF(server1:85214)<v1>:55157 is UNPROCESSED. Value in RVK6VVPKHF(server2:85228)<v2>:53458 is null

        at transactionsMixedWithNon//com.vmware.gemfire.examples.transactionsMixedWithNon.reproduce.VerifyBucketCopiesFunction.execute(VerifyBucketCopiesFunction.java:73)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.command.ExecuteFunction70.executeFunctionLocally(ExecuteFunction70.java:400)
        at gemfire//org.apache.geode.internal.cache.tier.sockets.command.ExecuteFunction70.cmdExecute(ExecuteFunction70.java:261)
        ... 9 more
```

5. Check for inconsistent entries using the provided functions

        $ gfsh
        $ connect
        $ execute function --id=VerifyBucketCopiesFunction --member --arguments=example-region
        $ execute function --id=FindOldEntriesFunction   --arguments=example-region,1


6. Shut down the system.
        $ gfsh run --file=scripts/stop.gfsh
