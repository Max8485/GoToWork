package org.maxsid.work.bot.kafka.service;

import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;

public interface KafkaConsumerService {

    void consumeUserSettingsFromCore(UserSettingsDto userSettingsDto, String userId);

    void consumeRouteCalculatedFromCore(RouteResponse routeResponse, String userId);
}
