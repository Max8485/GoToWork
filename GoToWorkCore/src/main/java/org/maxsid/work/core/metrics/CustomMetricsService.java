package org.maxsid.work.core.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public interface CustomMetricsService {

    void init();

    void setKafkaQueueSize(int size);

    int getKafkaQueueSize();

    void setSyncStatus(boolean isHealthy);

    int getSyncStatus();
}
