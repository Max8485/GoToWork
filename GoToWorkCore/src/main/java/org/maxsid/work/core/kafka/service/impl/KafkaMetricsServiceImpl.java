package org.maxsid.work.core.kafka.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.maxsid.work.core.kafka.service.KafkaMetricsService;
import org.maxsid.work.core.metrics.impl.CustomMetricsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaMetricsServiceImpl implements KafkaMetricsService {

    private final CustomMetricsServiceImpl metricsService;

    @Value("${spring.kafka.bootstrap-servers:localhost:9093}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:go-to-work-core-service}")
    private String groupId;

    private AdminClient adminClient;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        adminClient = AdminClient.create(props);
        log.info("✅ Kafka AdminClient created for metrics");
    }

    @PreDestroy
    public void destroy() {
        if (adminClient != null) {
            adminClient.close();
            log.info("✅ Kafka AdminClient closed");
        }
    }

    @Override
    @Scheduled(fixedDelay = 30000)
    public void checkKafkaQueueSize() {

        try {
            log.info("🔄 Checking Kafka queue size...");

            String[] topics = {"user-settings-from-bot-topic", "route-calculated-from-bot-topic"};
            int totalLag = 0;

            for (String topic : topics) {
                // Получаем описание топика
                var topicDescription = adminClient.describeTopics(Collections.singletonList(topic))
                        .allTopicNames().get().get(topic);

                if (topicDescription == null) {
                    log.warn("Topic {} does not exist", topic);
                    continue;
                }

                // Получаем конечные оффсеты для всех партиций
                Map<TopicPartition, Long> endOffsets = new HashMap<>();
                for (var partition : topicDescription.partitions()) {
                    TopicPartition tp = new TopicPartition(topic, partition.partition());
                    // ✅ Получаем конечный оффсет через другой запрос
                    endOffsets.put(tp, adminClient.listConsumerGroupOffsets(groupId)
                            .partitionsToOffsetAndMetadata()
                            .get()
                            .get(tp) != null ?
                            adminClient.listConsumerGroupOffsets(groupId)
                                    .partitionsToOffsetAndMetadata()
                                    .get()
                                    .get(tp)
                                    .offset() : 0);
                }

                // Получаем закоммиченные оффсеты группы
                Map<TopicPartition, OffsetAndMetadata> committedOffsets = adminClient
                        .listConsumerGroupOffsets(groupId)
                        .partitionsToOffsetAndMetadata()
                        .get();

                // Считаем lag
                for (var partition : topicDescription.partitions()) {
                    TopicPartition tp = new TopicPartition(topic, partition.partition());

                    long committedOffset = committedOffsets.containsKey(tp) ? committedOffsets.get(tp).offset() : 0;
                    long endOffset = endOffsets.getOrDefault(tp, 0L);

                    long lag = endOffset - committedOffset;
                    if (lag > 0) {
                        totalLag += lag;
                        log.info("Topic: {}, Partition: {}, Lag: {}", topic, partition.partition(), lag);
                    }
                }
            }

            metricsService.setKafkaQueueSize(totalLag);
            log.info("📊 Kafka queue size: {}", totalLag);

        } catch (Exception e) {
            log.error("❌ Failed to check Kafka queue size", e);
            metricsService.setKafkaQueueSize(-1);
        }
    }

    @Override
    public int getCurrentQueueSize() {
        return metricsService.getKafkaQueueSize();
    }
}
