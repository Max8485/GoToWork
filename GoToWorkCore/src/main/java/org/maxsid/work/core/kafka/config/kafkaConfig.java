package org.maxsid.work.core.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaConfig {

    @Bean
    NewTopic userSettingsTopic() {
        return TopicBuilder.name("user-settings-topic")
                .partitions(3)
                .replicas(1)
                .build();
    } @Bean
    NewTopic routeCalculateTopic() {
        return TopicBuilder.name("route-calculated-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
