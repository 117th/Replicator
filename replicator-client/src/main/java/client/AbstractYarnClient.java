package client;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.yarn.client.YarnClient;

@EnableAutoConfiguration
public abstract class AbstractYarnClient {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractYarnClient.class);
    protected static YarnClient yarnClient;
    protected static ApplicationId applicationId;
    protected static ConfigurableApplicationContext context;

    public static void runClient(String[] args) {
        context = SpringApplication.run(AbstractYarnClient.class, args);
        yarnClient = context.getBean(YarnClient.class);
        applicationId = yarnClient.submitApplication();
    }

}
