package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.repository.UserSettingsRepository;
import org.example.route.RouteRequest;
import org.example.route.RouteResponse;
import org.example.service.RouteCalculationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;
    private final UserSettingsRepository userSettingsRepository;

    @PostMapping("api/routes/calculate")
    public RouteResponse calculateResponse(@RequestBody RouteRequest request) { //работает!
        return routeCalculationService.calculateOptimalRoute(request); //рассчитать оптимальный маршрут
    }
}
