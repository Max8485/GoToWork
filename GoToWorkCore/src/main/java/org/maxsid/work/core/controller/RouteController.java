package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;

    @PostMapping("/users/{userId}/settings") //работает
    public ResponseEntity<UserSettingsDto> saveUserSettings(
            @PathVariable Long userId,
            @RequestBody RouteRequest request) {
        try {
            UserSettings savedSettings = routeCalculationService.saveUserSettings(userId, request);
            // Конвертируем в DTO
            UserSettingsDto responseDto = UserSettingsDto.builder() //заменить на mapper - userSettings в userSettingsDto.
                    .userId(savedSettings.getUserId())
                    .homeAddress(savedSettings.getHomeAddress())
                    .workAddress(savedSettings.getWorkAddress())
                    .arrivalTimeToWork(savedSettings.getArrivalTimeToWork())
                    .timeZone(savedSettings.getTimeZone())
                    .build();

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            log.error("Failed to save user settings for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/{userId}/calculate") //работает
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
    public ResponseEntity<UserSettingsDto> getUserSettings(@PathVariable Long userId) {
        Optional<UserSettings> userSettings = routeCalculationService.getUserSettings(userId);

        UserSettingsDto responseDto = new UserSettingsDto(
                userSettings.get().getUserId(),
                userSettings.get().getHomeAddress(),
                userSettings.get().getWorkAddress(),
                userSettings.get().getArrivalTimeToWork(),
                userSettings.get().getTimeZone());

        return ResponseEntity.ok(responseDto);
    }
}


