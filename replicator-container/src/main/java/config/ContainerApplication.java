package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ContainerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerApplication.class);

        public static void main(String[] args) {
            LOGGER.info("Container started");
            SpringApplication.run(ContainerApplication.class, args);
        }

}
