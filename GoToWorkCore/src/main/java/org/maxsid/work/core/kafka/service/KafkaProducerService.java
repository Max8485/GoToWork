package org.maxsid.work.core.kafka.service;

import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;

public interface KafkaProducerService {

    void sendUserSettingsSavedEvent(Long userId, UserSettingsDto userSettingsDto);

    void sendRouteCalculatedEvent(Long userId, RouteResponse routeResponse);


}
