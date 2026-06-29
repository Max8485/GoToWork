package org.maxsid.work.core.kafka.service;

public interface KafkaMetricsService {

    void checkKafkaQueueSize();

    int getCurrentQueueSize();

}
