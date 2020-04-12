package appmaster;

import appmaster.config.AppmasterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class AppmasterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppmasterApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Going to start appmaster");
        SpringApplication.run(AppmasterConfig.class, args);
    }
}
