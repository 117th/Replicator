package client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.yarn.client.YarnClient;

@EnableAutoConfiguration
public abstract class AbstractYarnClient {

    public static void main(String[] args) {
        SpringApplication.run(AbstractYarnClient.class, args)
                .getBean(YarnClient.class)
                .submitApplication();
    }

}
