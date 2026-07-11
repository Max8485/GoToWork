package org.maxsid.work.core;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class GoToWorkCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoToWorkCoreApplication.class);
    }
}