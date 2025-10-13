package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coordinates.Coordinates;
import org.example.route.RouteRequest;
import org.example.route.RouteResponse;
import org.example.service.GeocodeService;
import org.example.service.RouteCalculationService;
import org.example.service.RouteService;
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

    //http://localhost:8080/api/debug/routing?address1=Москва, Кутузовский проспект 32&address2=Москва, Красная площадь 1"

    private final GeocodeService geocodingService;
    private final RouteService routingService;
    private final RouteCalculationService routeCalculationService;

    // Тест геокодирования
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

            // Логируем координаты для отладки
            log.debug("Received coordinates - start: {},{}, end: {},{}",
                    start.getLat(), start.getLon(), end.getLat(), end.getLon());

            String startFormatted = String.format(Locale.US, "%.6f,%.6f", start.getLat(), start.getLon());
            String endFormatted = String.format(Locale.US, "%.6f,%.6f", end.getLat(), end.getLon());
            log.debug("Formatted coordinates - start: {}, end: {}", startFormatted, endFormatted);

            Integer duration = routingService.calculateTravelTimeToWork(start, end);

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

//    @PostMapping("/route")
//    public ResponseEntity<Map<String, Object>> testRoute(@RequestBody Map<String, Object> request) {
//        try {
//            Map<String, Object> startMap = (Map<String, Object>) request.get("start");
//            Map<String, Object> endMap = (Map<String, Object>) request.get("end");
//
//            Coordinates start = new Coordinates(
//                    Double.parseDouble(startMap.get("lat").toString()),
//                    Double.parseDouble(startMap.get("lon").toString())
//            );
//
//            Coordinates end = new Coordinates(
//                    Double.parseDouble(endMap.get("lat").toString()),
//                    Double.parseDouble(endMap.get("lon").toString())
//            );
//
//            Integer duration = routingService.calculateTravelTimeToWork(start, end);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("start", start);
//            response.put("end", end);
//            response.put("duration_seconds", duration);
//            response.put("duration_minutes", duration / 60);
//            response.put("start_formatted", String.format("%.6f,%.6f", start.getLat(), start.getLon()));
//            response.put("end_formatted", String.format("%.6f,%.6f", end.getLat(), end.getLon()));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Route debug failed", e);
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }

    // Тест расчета времени пути
//    @GetMapping("/routing")
//    public String testRouting(@RequestParam String address1,
//                              @RequestParam String address2) {
//        try {
//            Coordinates coord1 = geocodingService.geocodeAddress(address1);
//            Coordinates coord2 = geocodingService.geocodeAddress(address2);
//
//            Integer travelTimeSeconds = routingService.calculateTravelTimeToWork(coord1, coord2);
//            int travelTimeMinutes = travelTimeSeconds / 60;
//
//            return String.format(
//                    "✅ Маршрут: %s → %s\n" +
//                            "Координаты 1: %s\n" +
//                            "Координаты 2: %s\n" +
//                            "Время в пути: %d минут",
//                    address1, address2, coord1, coord2, travelTimeMinutes);
//
//        } catch (Exception e) {
//            return "❌ Ошибка расчета маршрута: " + e.getMessage();
//        }
//    }
//
//    // Полный тест расчета
//    @PostMapping("/full-test")
//    public RouteResponse fullTest(@RequestBody RouteRequest request) {
//        return routeCalculationService.calculateOptimalRoute(request);
//    }
}
