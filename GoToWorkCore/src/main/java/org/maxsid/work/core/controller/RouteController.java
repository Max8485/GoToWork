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

@RequestMapping("/api/routes")
@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;

    @PostMapping("/users/{userId}/settings") //работает
    public ResponseEntity<UserSettings> saveUserSettings(
            @PathVariable Long userId,
            @RequestBody RouteRequest request) {

        try {
            UserSettings savedSettings = routeCalculationService.saveUserSettings(userId, request);
            return ResponseEntity.ok(savedSettings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{userId}/calculate") //работает даже с Калиниградом
    public ResponseEntity<RouteResponse> calculateRoute(@PathVariable Long userId) {
        try {
            RouteResponse response = routeCalculationService.calculateOptimalRoute(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new RouteResponse());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users/{userId}/settings") //работает
    public ResponseEntity<UserSettings> getUserSettings(@PathVariable Long userId) {
        var settings = routeCalculationService.getUserSettings(userId);
        return settings.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


