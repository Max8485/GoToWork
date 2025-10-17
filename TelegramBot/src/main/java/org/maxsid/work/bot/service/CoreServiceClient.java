package org.maxsid.work.bot.service;

import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;
import org.maxsid.work.core.entity.UserSettings;

public interface CoreServiceClient {

    UserSettings saveUserSettings(Long userId, RouteRequest request);

    RouteResponse calculateRoute(Long userId);

    UserSettings getUserSettings(Long userId);
}
