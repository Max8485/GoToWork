package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.route.RouteRequest;
import org.example.route.RouteResponse;
import org.example.service.RouteCalculationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RouteController {

    //установи git!
    //сделать таблицу user_settings
    //сделать таблицу response, чтобы можно было проверить работу расчетов. Для этого response надо сделать как Entity,
    //и потом сделать responseDto, или оставить просто response.

    private final RouteCalculationService routeCalculationService;

    @PostMapping("api/routes/calculate")
    public RouteResponse calculateResponse(@RequestBody RouteRequest request) {
        return routeCalculationService.calculateOptimalRoute(request); //рассчитать оптимальный маршрут
    }
}
