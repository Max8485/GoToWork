package org.maxsid.work.bot.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic userSettingsTopic() {
        return TopicBuilder.name("user-settings-from-bot-topic")
                .partitions(3)
                .replicas(1)
                .build();
    } @Bean
    NewTopic routeCalculateTopic() {
        return TopicBuilder.name("route-calculated-from-bot-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
