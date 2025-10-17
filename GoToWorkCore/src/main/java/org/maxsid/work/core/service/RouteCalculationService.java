package org.maxsid.work.core.service;

import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;
import org.maxsid.work.core.entity.UserSettings;

import java.util.Optional;

public interface RouteCalculationService {

    RouteResponse calculateOptimalRoute(Long userId);

    UserSettings saveUserSettings(Long userId, RouteRequest request);

    Optional<UserSettings> getUserSettings(Long userId);

//    void calculateDailyRoutes();

//    void sendDailyNotifications();
}
