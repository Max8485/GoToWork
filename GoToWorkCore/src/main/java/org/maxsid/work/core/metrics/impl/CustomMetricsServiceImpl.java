package org.maxsid.work.core.metrics.impl;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.metrics.CustomMetricsService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomMetricsServiceImpl implements CustomMetricsService {

    private final MeterRegistry meterRegistry;

    private final AtomicInteger kafkaQueueSize = new AtomicInteger(0);
    private final AtomicInteger syncStatus = new AtomicInteger(1); // 1 = OK, 0 = ERROR

    @PostConstruct
    @Override
    public void init() {
        meterRegistry.gauge("kafka.queue.size",
                Tags.of("application", "go-to-work-core"),
                kafkaQueueSize,
                AtomicInteger::get);

        log.info("✅ kafka.queue.size registered");

        meterRegistry.gauge("sync.status",
                Tags.of("application", "go-to-work-core"),
                syncStatus,
                AtomicInteger::get);

        log.info("✅ sync.status registered");
    }

    @Override
    public void setKafkaQueueSize(int size) {
        kafkaQueueSize.set(size);
        log.info("📊 Kafka queue size updated: {}", size);
    }

    @Override
    public int getKafkaQueueSize() {
        return kafkaQueueSize.get();
    }

    @Override
    public void setSyncStatus(boolean isHealthy) {
        syncStatus.set(isHealthy ? 1 : 0);
        log.info("📊 Sync status updated: {}", isHealthy ? "OK" : "ERROR");
    }

    @Override
    public int getSyncStatus() {
        return syncStatus.get();
    }
}
