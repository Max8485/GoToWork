package org.maxsid.work.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableScheduling
//@EnableJpaAuditing
@SpringBootApplication
public class GoToWorkCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoToWorkCoreApplication.class);
    }
}