package org.maxsid.work.core.service;

import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;

import java.util.Optional;

public interface RouteCalculationService {

    RouteResponse calculateOptimalRoute(Long userId);

    UserSettings saveUserSettings(Long userId, RouteRequest request);

    Optional<UserSettings> getUserSettings(Long userId);
}
