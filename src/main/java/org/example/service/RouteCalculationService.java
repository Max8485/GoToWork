package org.example.service;

import org.example.route.RouteRequest;
import org.example.route.RouteResponse;

public interface RouteCalculationService {

    RouteResponse calculateOptimalRoute(RouteRequest request);
//    void calculateDailyRoutes();

    void sendDailyNotifications();
}
