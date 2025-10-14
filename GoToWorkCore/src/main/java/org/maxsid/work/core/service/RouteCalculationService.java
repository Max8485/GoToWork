package org.maxsid.work.core.service;

import org.maxsid.work.core.route.RouteRequest;
import org.maxsid.work.core.route.RouteResponse;

public interface RouteCalculationService {

    RouteResponse calculateOptimalRoute(RouteRequest request);
//    void calculateDailyRoutes();

    void sendDailyNotifications();
}
