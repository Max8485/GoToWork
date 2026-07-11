package org.maxsid.work.core.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.service.MetricsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MetricsServiceImpl implements MetricsService {

    private final MeterRegistry meterRegistry;

    @Override
    public void updateSyncStatus(boolean success) {
        meterRegistry.gauge("sync.status", success ? 1 : 0);
    }
}
