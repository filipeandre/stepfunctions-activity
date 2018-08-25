# AWS Step Functions with Activity

An Activity is one of the two task types in Step Functions. Read more here: 
* [Tasks](https://docs.aws.amazon.com/step-functions/latest/dg/concepts-tasks.html)

It created an Activity with a TimeoutSeconds of 150 and a Heartbeat timeout of 30 seconds.

## Prerequisites
* [SAM CLI](https://github.com/awslabs/serverless-application-model) installed.

## How to run
First create an S3 bucket with some name. Then edit `package-and-deploy.sh.tmp` and replace <BUCKET_NAME> with that name.
Then rename  `package-and-deploy.sh.tmp` to `package-and-deploy.sh` and run `sh package-and-deploy.sh`

