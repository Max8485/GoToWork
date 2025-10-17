package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.UserSettingsService;
import org.maxsid.work.core.service.impl.RouteCalculationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;
    private final RouteCalculationServiceImpl routeCalculationServiceImpl;
    private final UserSettingsService userSettingsService;


    @PostMapping("api/routes/calculate")
    public RouteResponse calculateResponse(@RequestBody RouteRequest request) { //работает!
        return routeCalculationService.calculateOptimalRoute(request); //рассчитать оптимальный маршрут
    }

    @PostMapping("api/routes/users/settings") //работает. Сделать с userId
    public void saveUserSettings(
            @RequestBody RouteRequest request) {
        userSettingsService.saveUserSettingsForBot(request);
    }

    @GetMapping("api/routes/users/{userId}/settings") //работает
    public ResponseEntity<UserSettings> getUserSettings(@PathVariable(value = "userId") Long userId) {
        Optional<UserSettings> userSettings = routeCalculationServiceImpl.getUserSettings(userId);
        return userSettings.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


