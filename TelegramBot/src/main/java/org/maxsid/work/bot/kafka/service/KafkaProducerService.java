package org.maxsid.work.bot.kafka.service;

import org.maxsid.work.dto.RouteRequest;

public interface KafkaProducerService {

    void sendUserSettingsToCore(Long chatId, RouteRequest request);

    void sendRouteCalculationRequestToCore(Long chatId, RouteRequest routeRequest);
}
