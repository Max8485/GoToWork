package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.model.GraphHopperResponse;
import org.maxsid.work.core.service.RouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class RouteServiceImpl implements RouteService {

    @Value("${app.graphhooper.api-key}")
    private String graphApiKey;

    @Value("${app.graphhooper.url}")
    private String graphUrl;

    private final RestTemplate restTemplate;

    public Long calculateTravelTimeToWork(Coordinates from, Coordinates to) {
        String url = UriComponentsBuilder.fromHttpUrl(graphUrl)
                .queryParam("point", from.getLat() + "," + from.getLon())
                .queryParam("point", to.getLat() + "," + to.getLon())
                .queryParam("vehicle", "car")
                .queryParam("key", graphApiKey)
                .queryParam("calc_points", false)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("paths") && ((java.util.List) body.get("paths")).size() > 0) {
                    Map<String, Object> path = (Map<String, Object>) ((java.util.List) body.get("paths")).get(0);
                    if (path.containsKey("time")) {
                        long timeMs = ((Number) path.get("time")).longValue();
                        return timeMs / 1000 / 60; // Конвертируем в минуты
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating route: " + e.getMessage());
        }

        // Fallback: 30 минут
        return 30L;
    }



//    @Override
//    public Integer calculateTravelTimeToWork(Coordinates start, Coordinates end) {
//
//        try {
//            log.debug("Calculating route from {} to {}", start, end);
//
//            String url = buildGraphHopperUrl(start, end);
//            log.debug("GraphHopper URL: {}", url.replace(graphApiKey, "***"));
//
//            ResponseEntity<GraphHopperResponse> response = restTemplate.getForEntity(url, GraphHopperResponse.class);
//
//            return processGraphHopperResponse(response.getBody());
//
//        } catch (Exception e) {
//            log.error("Routing calculation failed", e);
//            throw new RuntimeException("Routing calculation failed: " + e.getMessage(), e);
//        }
//    }
//
//    private String buildGraphHopperUrl(Coordinates start, Coordinates end) {
//        // Правильное форматирование координат с ТОЧКАМИ и английской локалью
//        String startPoint = String.format(Locale.US, "%.6f,%.6f", start.getLat(), start.getLon());
//        String endPoint = String.format(Locale.US, "%.6f,%.6f", end.getLat(), end.getLon());
//
//        log.debug("Formatted points - start: {}, end: {}", startPoint, endPoint);
//
//        return UriComponentsBuilder.fromHttpUrl(graphUrl)
//                .queryParam("point", startPoint)
//                .queryParam("point", endPoint)
//                .queryParam("vehicle", "car")
//                .queryParam("locale", "ru")
//                .queryParam("key", graphApiKey)
//                .queryParam("calc_points", "false")
//                .queryParam("instructions", "false")
//                .queryParam("points_encoded", "false")
//                .encode()
//                .toUriString();
//    }
//
//    private Integer processGraphHopperResponse(GraphHopperResponse response) {
//        if (response == null) {
//            throw new RuntimeException("Empty response from GraphHopper API");
//        }
//
//        if (response.getMessage() != null) {
//            throw new RuntimeException("GraphHopper API error: " + response.getMessage());
//        }
//
//        if (response.getPaths() == null || response.getPaths().isEmpty()) {
//            throw new RuntimeException("No routes found in GraphHopper response");
//        }
//
//        var bestRoute = response.getPaths().get(0);
//
//        if (bestRoute.getTime() == null) {
//            throw new RuntimeException("No time information in GraphHopper route");
//        }
//
//        // Конвертируем миллисекунды в секунды
//        int travelTimeSeconds = (int) (bestRoute.getTime() / 1000);
//
//        log.info("Route calculated: {} seconds ({} minutes)", travelTimeSeconds, travelTimeSeconds / 60);
//
//        return travelTimeSeconds;
//    }








//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", twoGisApiKey);
//
//            String points = String.format("%s,%s~%s,%s",
//                    start.getLat(), start.getLon(), end.getLat(), end.getLon());
//
//            String url = String.format("%s/get_route?points=%s&type=car&traffic=true",
//                    twoGisUrl, points);
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<TwoGisResponse> response = restTemplate.exchange(
//                    url, HttpMethod.GET, entity, TwoGisResponse.class);
//
//            if (response.getBody() != null && response.getBody().getResult() != null &&
//                    response.getBody().getResult().getTotal_duration() != null) {
//                return response.getBody().getResult().getTotal_duration(); // in seconds
//            }
//            throw new RuntimeException("No route duration found");
//
//        } catch (Exception e) {
//            throw new RuntimeException("Routing calculation failed: " + e.getMessage(), e);
//        }
//    }
}
