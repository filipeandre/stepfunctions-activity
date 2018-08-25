package org.saabye_pedersen.activity;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.*;

import java.util.concurrent.TimeUnit;

public class Activity {

    private final static String ACTIVITY_ARN = System.getProperty("ACTIVITY_ARN");
    //if false it will fail due to a missing heartbeat
    private final static boolean SEND_HEARTBEAT = Boolean.parseBoolean(System.getProperty("SEND_HEARTBEAT", "true"));
    //set to 8 or higher to force a max task timeout
    private static final int LOOP_COUNT = Boolean.parseBoolean(System.getProperty("TIMEOUT", "false")) ? 8 : 5;

    public static void main(final String[] args) throws Exception {

        if (ACTIVITY_ARN == null || ACTIVITY_ARN.trim().isEmpty()) {
            System.err.println("Your must provide an ARN to the Activity.");
            return;
        }

        ClientConfiguration clientConfiguration = getClientConfiguration();
        AWSStepFunctions client = getAwsStepFunctionsClient(clientConfiguration);

        while (true) {

            // Longs polls for up to 60 seconds (which is my we above set the sockettimeout to more than this)
            GetActivityTaskResult getActivityTaskResult =
                    client.getActivityTask(
                            new GetActivityTaskRequest().withActivityArn(ACTIVITY_ARN).withWorkerName("My Java Worker"));

            if (getActivityTaskResult.getTaskToken() != null) {

                System.out.println(String.format("Starting task with token %s", getActivityTaskResult.getTaskToken()));
                System.out.flush();

                try {

                    for (int i = 0; i <= LOOP_COUNT; i++) {
                        System.out.println("Start sleeping");
                        Thread.sleep(20_000);
                        System.out.println("Woke up");
                        if (SEND_HEARTBEAT) {
                            System.out.println("Sending heartbeat");
                            client.sendTaskHeartbeat(new SendTaskHeartbeatRequest().withTaskToken(getActivityTaskResult.getTaskToken()));
                        } else {
                            System.out.println("Not sending heartbeat");
                        }
                    }

                    String greetingResult = Activity.echo(getActivityTaskResult.getInput());

                    client.sendTaskSuccess(
                            new SendTaskSuccessRequest().withOutput(
                                    greetingResult).withTaskToken(getActivityTaskResult.getTaskToken()));

                } catch (Exception e) {
                    System.err.println(String.format("Exception, failing execution. Message:\n%s", e.getMessage()));

                    try {
                        client.sendTaskFailure(new SendTaskFailureRequest().withTaskToken(
                                getActivityTaskResult.getTaskToken())
                                .withError(e.getMessage()));
                    } catch (Exception e1) {
                        System.out.println("Sending TaskFailure failed, probably already timed failed");
                        System.err.println(String.format("Exception, failing execution. Message:\n%s", e.getMessage()));
                    }
                }
            } else {
                System.out.println("Poll did not get any tasks, trying again.");
            }
        }
    }

    private static ClientConfiguration getClientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));
        return clientConfiguration;
    }

    private static AWSStepFunctions getAwsStepFunctionsClient(ClientConfiguration clientConfiguration) {
        return AWSStepFunctionsClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withClientConfiguration(clientConfiguration)
                .build();
    }

    private static String echo(String inputAsString) throws Exception {
        return "{\"echo\": " + inputAsString + "}";
    }
}