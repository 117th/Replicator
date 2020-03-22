package client;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.yarn.client.YarnClient;

@EnableAutoConfiguration
public class ClientApplication {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);
    protected static YarnClient yarnClient;
    protected static ApplicationId applicationId;
    protected static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        LOGGER.info("Starting an app");
        context = SpringApplication.run(ClientApplication.class, args);
        yarnClient = context.getBean(YarnClient.class);
        applicationId = yarnClient.submitApplication();
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
