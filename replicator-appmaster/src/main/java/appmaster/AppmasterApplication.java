package appmaster;

import appmaster.config.AppmasterConfig;
import org.springframework.boot.SpringApplication;

public class AppmasterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppmasterConfig.class, args);
    }
}
