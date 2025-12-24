package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.service.RouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class); //исправить!
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

        return 30L;
    }
}
