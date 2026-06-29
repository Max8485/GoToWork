package org.maxsid.work.core.metrics.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.metrics.CustomMetricsService;
import org.maxsid.work.core.metrics.SyncHealthCheckService;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.RouteService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncHealthCheckServiceImpl implements SyncHealthCheckService {

    private final GeocodeService geocodeService;
    private final RouteService routeService;
    private final CustomMetricsService metricsService;

    // ✅ Кэшируем последний успешный результат
    private boolean lastSyncStatus = true;
    private LocalDateTime lastCheckTime;

    @Scheduled(fixedDelay = 30000)
    public void checkSyncStatus() {
        try {
            // ✅ Проверяем только если прошло > 5 минут
            if (lastCheckTime != null &&
                    Duration.between(lastCheckTime, LocalDateTime.now()).toMinutes() < 5) {
                return;
            }

            // ✅ Используем тестовые данные
            String testAddress = "Москва, Тверская, 1";
            geocodeService.geocodeAddress(testAddress);

            Coordinates home = new Coordinates(55.751244, 37.618423);
            Coordinates work = new Coordinates(55.757133, 37.614374);
            routeService.calculateTravelTimeToWork(home, work);

            // ✅ Успешно
            metricsService.setSyncStatus(true);
            lastSyncStatus = true;
            lastCheckTime = LocalDateTime.now();
            log.debug("✅ Sync status: OK");

        } catch (Exception e) {
            log.error("❌ Sync check failed: {}", e.getMessage());
            metricsService.setSyncStatus(false);
            lastSyncStatus = false;
        }
    }
}
