package org.maxsid.work.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoToWorkCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoToWorkCoreApplication.class);
    }
}