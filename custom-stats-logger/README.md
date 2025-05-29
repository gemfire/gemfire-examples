# Stats Logging 

This example provides a way to log selected GemFire statistics at a configurable interval. You control which stats are captured by editing the statLogger.properties file. Once the StatLogger function is deployed, it will log the specified statistics on each member at the interval you've defined.

## Configuring and Running the Stats Logger
To control what statistics are logged and how often, edit the `statLogger.properties` file located at:
```
stats-logger/src/resources/statLogger.properties
```
### Example `statLogger.properties` File:
```properties
# Log frequency in milliseconds (defaults to 5000 if not set)
logFrequency = 6000

# Comma-separated list of statistic names to log
# Leave this property unset if you don't want the StatLogger to run
statistics = DistributionStats.replyWaitsInProgress,StatSampler.delayDuration
```
> If the statistics property is not set, the logger will not run.

---

## Build the StatLogger Function
Once your `statLogger.properties` file is configured:

```
cd gemfire-examples/stats-logger
./gradlew clean build
```
This will generate `custom-stats-logger-1.0-SNAPSHOT.jar` in the `build/libs` directory.

---

## Start the GemFire Cluster and Deploy the Function
Start the sample cluster:

```
cd gemfire-examples/stats-logger/scripts
./startGemFire.sh
```

Then connect with `gfsh` and deploy the JAR:
```
> gfsh
    _________________________     __
   / _____/ ______/ ______/ /____/ /
  / /  __/ /___  /_____  / _____  /
 / /__/ / ____/  _____/ / /    / /  
/______/_/      /______/_/    /_/    10.1.0

Monitor and Manage VMware GemFire

gfsh> connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=cblack-z01, port=1099] ..
Successfully connected to: [host=cblack-z01, port=1099]

You are connected to a cluster of version 10.1.0.

gfsh> deploy --jar ../build/libs/custom-stats-logger-1.0-SNAPSHOT.jar

Deploying files: custom-stats-logger-1.0-SNAPSHOT.jar
Total file size is: 0.01MB

Continue?  (Y/n): Y
Member  |                 JAR                  | JAR Location
------- | ------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------
server1 | custom-stats-logger-1.0-SNAPSHOT.jar | /private/var/folders/96/bgs8ylk14m5cbz7twpvwb9hr0000gq/T/gemfire-extensions7730695269553751758/custom-stats-logger/main/libs/custom-stats-logger-1.0-SNAPSHOT.jar
server2 | custom-stats-logger-1.0-SNAPSHOT.jar | /private/var/folders/96/bgs8ylk14m5cbz7twpvwb9hr0000gq/T/gemfire-extensions14066204110947319168/custom-stats-logger/main/libs/custom-stats-logger-1.0-SNAPSHOT.jar
```
You should see confirmation that the JAR has been deployed to each member.

---

## Run the StatLogger Function
```
gfsh> execute function --id=StatLogger
```
Example output:
```
Member  | Status | Message
------- | ------ | -----------------------------------------------------------
server1 | OK     | [Logging Metric count 1 with timer interval set to 6000 ms]
server2 | OK     | [Logging Metric count 1 with timer interval set to 6000 ms]
```
---

## Sample Output from server1.log


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
---

## Updating and Redeploying

If you change either the `StatLogger` function or the `statLogger.properties` file:

1. Rebuild the JAR:
```
./gradlew clean build
```

2. Redeploy it using gfsh:
```
deploy --jar ../build/libs/custom-stats-logger-1.0-SNAPSHOT.jar
```
