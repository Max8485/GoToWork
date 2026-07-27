package org.maxsid.work.bot.service;

import org.maxsid.work.dto.*;


public interface CoreServiceClient {

    UserSettingsDto saveUserSettings(Long userId, RouteRequest request);

    RouteResponse calculateRoute(Long userId);

    UserSettingsDto getUserSettings(Long userId);

    EnableNotificationsDto enableNotifications(Long userId, boolean enabled);

}
