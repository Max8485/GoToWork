package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/debug")
public class DebugController {

    private final GeocodeService geocodingService;
    private final RouteService routingService;

    @GetMapping("/geocode")
    public String testGeocode(@RequestParam String address) { //геокод работает!
        try {
            Coordinates coord = geocodingService.geocodeAddress(address);
            return String.format("✅ Адрес: %s\nКоординаты: lat=%f, lon=%f",
                    address, coord.getLat(), coord.getLon());
        } catch (Exception e) {
            return "❌ Ошибка геокодирования: " + e.getMessage();
        }
    }

    @PostMapping("/route") //работает!
    public ResponseEntity<Map<String, Object>> testRoute(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> startMap = (Map<String, Object>) request.get("start");
            Map<String, Object> endMap = (Map<String, Object>) request.get("end");

            Coordinates start = new Coordinates(
                    Double.parseDouble(startMap.get("lat").toString()),
                    Double.parseDouble(startMap.get("lon").toString())
            );

            Coordinates end = new Coordinates(
                    Double.parseDouble(endMap.get("lat").toString()),
                    Double.parseDouble(endMap.get("lon").toString())
            );

            log.debug("Received coordinates - start: {},{}, end: {},{}",
                    start.getLat(), start.getLon(), end.getLat(), end.getLon());

            String startFormatted = String.format(Locale.US, "%.6f,%.6f", start.getLat(), start.getLon());
            String endFormatted = String.format(Locale.US, "%.6f,%.6f", end.getLat(), end.getLon());
            log.debug("Formatted coordinates - start: {}, end: {}", startFormatted, endFormatted);

            Long duration = routingService.calculateTravelTimeToWork(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("start", start);
            response.put("end", end);
            response.put("duration_seconds", duration);
            response.put("duration_minutes", duration / 60);
            response.put("start_formatted", startFormatted);
            response.put("end_formatted", endFormatted);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Route debug failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
