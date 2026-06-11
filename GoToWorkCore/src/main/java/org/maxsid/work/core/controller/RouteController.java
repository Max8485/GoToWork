package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.mapper.UserSettingsMapper;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@RestController
public class RouteController {

    private final RouteCalculationService routeCalculationService;
    private final UserSettingsMapper userSettingsMapper;

    @PostMapping("/users/{userId}/settings") //работает
    public ResponseEntity<UserSettingsDto> saveUserSettings(
            @PathVariable Long userId,
            @RequestBody RouteRequest request) {
            UserSettings savedSettings = routeCalculationService.saveUserSettings(userId, request);
            UserSettingsDto responseDto = userSettingsMapper.mapUserSettingsToDto(savedSettings); //новый вариант, ПРОВЕРЬ!

            return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/users/{userId}/calculate") //работает
    public ResponseEntity<RouteResponse> calculateRoute(@PathVariable Long userId) {
            RouteResponse response = routeCalculationService.calculateOptimalRoute(userId);
            return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/settings") //работает
    public ResponseEntity<UserSettingsDto> getUserSettings(@PathVariable Long userId) {
        return routeCalculationService.getUserSettings(userId)      //НОВЫЙ КОД, ПРОВЕРЬ!
                .map(userSettingsMapper::mapUserSettingsToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


