package com.backend.allreva.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.backend.allreva")
public class BatchServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchServerApplication.class, args);
    }
}
