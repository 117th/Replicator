package config;

import core.container.vo.ContainerRWContext;
import messageHandler.impl.BasicMessageHandler;
import org.apache.hadoop.yarn.server.api.ContainerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
public class ContainerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerApplication.class);

        public static void main(String[] args) {
            LOGGER.info("Container started");
            SpringApplication.run(ContainerApplication.class, args);
        }

        @Bean
        public BasicMessageHandler basicMessageHandler(ContainerRWContext context) {
            return new BasicMessageHandler(context);
        }

}
