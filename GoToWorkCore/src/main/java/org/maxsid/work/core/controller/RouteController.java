package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.route.RouteRequest;
import org.maxsid.work.core.route.RouteResponse;
import org.maxsid.work.core.service.RouteCalculationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;

    @PostMapping("api/routes/calculate")
    public RouteResponse calculateResponse(@RequestBody RouteRequest request) { //работает!
        return routeCalculationService.calculateOptimalRoute(request); //рассчитать оптимальный маршрут
    }
}
