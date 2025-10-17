package org.maxsid.work.core.service;

import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;

public interface RouteCalculationService {

    RouteResponse calculateOptimalRoute(RouteRequest request);
//    void calculateDailyRoutes();

    void sendDailyNotifications();
}
