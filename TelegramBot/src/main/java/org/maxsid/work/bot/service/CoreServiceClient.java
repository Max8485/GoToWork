package org.maxsid.work.bot.service;

import org.maxsid.work.bot.dto.RouteResponse;
import org.maxsid.work.bot.dto.RouteRequest;

public interface CoreServiceClient {

    void saveUserSettings(Long userId, RouteRequest request);

    RouteResponse calculateRoute(Long userId);

    RouteRequest getUserSettings(Long userId);
}
