# AWS Step Functions Activity

## How to run
Run `mvn clean install` and then:
`java -DACTIVITY_ARN=<ARN> -DSEND_HEARTBEAT=<true | false> -DTIMEOUT=<true | false> -jar target/activity-1.0.jar`

Arguments:
 * ACTIVITY_ARN: *Mandatory* ARN of the activity referenced in the State Machine. 
 * SEND_HEARTBEAT: *Optional* if set to false, the invocation will fail due to missing heartbeats. Defaults to true.
 * TIMEOUT: *Optional* if set to true, the invocation will fail as invocation takes longer than 150 seconds. Defaults to false. Note that if SEND_HEARTBEAT is false, that will trigger before the TIMEOUT. 

## Prerequisites 
* Java 8+
* Maven
 
## Note
The code is only demo code (obviously), it uses no threading and has a lot of System.outs to provide insight.