# Stats Logging 

Use statLogger.properties file to change the logFrequency or update stats list.
This properties file is located at stats-logger/src/resources

```
# how often to log the statistic values in seconds
# logFrequency in ms, defaults to 5000 if not set
logFrequency = 6000

# names of the statistics to log as a comma separated list
# Do not set this property if you don't want StatLogger to run.
statistics = DistributionStats.replyWaitsInProgress,StatSampler.delayDuration
```
If you don't want the stats logger to run do not set the statistics property.
Once you have set the required properties in statLogger.properties file, you need to build the jar.

```
cd gemfire-examples/stats-logger
./gradlew clean build

```
This will create the custom-stats-logger-1.0-SNAPSHOT.jar in the build/lib folder.

```
> cd gemfire-examples/stats-logger/scripts
> ./startGemFire.sh

> gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /  
/______/_/      /______/_/    /_/    10.1.0

Monitor and Manage VMware GemFire

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=cblack-z01, port=1099] ..
Successfully connected to: [host=cblack-z01, port=1099]

You are connected to a cluster of version 10.1.0.

gfsh>deploy --jar ../build/libs/custom-stats-logger-1.0-SNAPSHOT.jar

Deploying files: custom-stats-logger-1.0-SNAPSHOT.jar
Total file size is: 0.01MB

Continue?  (Y/n): Y
Member  |                 JAR                  | JAR Location
------- | ------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------
server1 | custom-stats-logger-1.0-SNAPSHOT.jar | /private/var/folders/96/bgs8ylk14m5cbz7twpvwb9hr0000gq/T/gemfire-extensions7730695269553751758/custom-stats-logger/main/libs/custom-stats-logger-1.0-SNAPSHOT.jar
server2 | custom-stats-logger-1.0-SNAPSHOT.jar | /private/var/folders/96/bgs8ylk14m5cbz7twpvwb9hr0000gq/T/gemfire-extensions14066204110947319168/custom-stats-logger/main/libs/custom-stats-logger-1.0-SNAPSHOT.jar

gfsh>execute function --id=StatLogger
Member  | Status | Message
------- | ------ | -----------------------------------------------------------
server1 | OK     | [Logging Metric count 1 with timer interval set to 6000 ms]
server2 | OK     | [Logging Metric count 1 with timer interval set to 6000 ms]
```

## Example Logging on Each Member  
server1.log

```
[info 2024/07/31 20:28:58.305 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 1002

[info 2024/07/31 20:29:04.305 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0

[info 2024/07/31 20:29:04.306 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 1003

[info 2024/07/31 20:29:10.305 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0

[info 2024/07/31 20:29:10.306 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 998

[info 2024/07/31 20:29:16.305 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0

[info 2024/07/31 20:29:16.306 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 1001

[info 2024/07/31 20:29:22.302 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0

[info 2024/07/31 20:29:22.303 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 1003

[info 2024/07/31 20:29:28.305 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0

[info 2024/07/31 20:29:28.306 GMT-04:00 server1 <StatLogger> tid=0x100] StatSampler.delayDuration = 1002

[info 2024/07/31 20:29:34.305 GMT-04:00 server1 <StatLogger> tid=0x100] DistributionStats.replyWaitsInProgress = 0
```

If you make any changes to stats logger function/statLogger.properties you will have to re-build and redeploy the custom-stats-logger-1.0-SNAPSHOT.jar
You can do that using the same set of steps described in the start of this page.