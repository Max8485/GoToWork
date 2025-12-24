package org.maxsid.work.bot.service;

import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;


public interface CoreServiceClient {

    UserSettingsDto saveUserSettings(Long userId, RouteRequest request);

    RouteResponse calculateRoute(Long userId);

    UserSettingsDto getUserSettings(Long userId);
}
