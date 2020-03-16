package client.impl;

import client.AbstractYarnClient;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.springframework.yarn.client.YarnClient;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ClientApplication extends AbstractYarnClient {

    public static void main(String[] args) {
        runClient(args);
        monitor();
    }

    private static void monitor() {
        YarnApplicationState state = null;
        FinalApplicationStatus status = null;

        while (state != YarnApplicationState.FINISHED && state != YarnApplicationState.KILLED && state != YarnApplicationState.FAILED) {
            ApplicationReport report = yarnClient.getApplicationReport(applicationId);
            state = report.getYarnApplicationState();
            status = report.getFinalApplicationStatus();

            LOGGER.info("Current status of {}: {}", applicationId, report);
        }

        //is it possible for our application to finish with succeeded? I don't think so
        if (state == YarnApplicationState.FINISHED && status == FinalApplicationStatus.SUCCEEDED) {
            LOGGER.info("Appmaster finished without troubles");
            context.close();
        } else {
            LOGGER.error("Something went terribly wrong and appmaster" +
                    "finished with errors.");
            context.close();
            throw new IllegalStateException("Appmaster down");
        }
    }

}
