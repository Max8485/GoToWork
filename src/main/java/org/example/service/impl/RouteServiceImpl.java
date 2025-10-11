package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.coordinates.Coordinates;
import org.example.feign.TwoGisFeignClient;
import org.example.service.RouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class RouteServiceImpl implements RouteService {

    @Value("${app.twogis.api-key}")
    private String twoGisApiKey;

    private final TwoGisFeignClient twoGisFeignClient;
    @Override
    public Integer calculateTravelTimeToWork(Coordinates start, Coordinates end) {
        try {
            String points = String.format("%s,%s~%s,%s", start.getLat(), start.getLon(), end.getLat(), end.getLon());
            String authorization = twoGisApiKey; //добавить "Token " ?

            Map<String, Object> response = twoGisFeignClient.calculateRoute(authorization, points, "car", true);

            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");

                if (result.containsKey("total_duration")) {
                    return (Integer) result.get("total_duration");
                }
            }
            throw new RuntimeException("Не удалось рассчитать продолжительность маршрута");

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расчете маршрута : " + e.getMessage(), e);
        }
    }
}
