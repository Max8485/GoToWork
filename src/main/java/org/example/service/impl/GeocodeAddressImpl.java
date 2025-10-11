package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.coordinates.Coordinates;
import org.example.feign.DadataFeignClient;
import org.example.service.GeocodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GeocodeAddressImpl implements GeocodeService {

    @Value("${app.dadata.api-key}")
    private String daDataApiKey;

    private final DadataFeignClient dadataFeignClient;
    @Override
    public Coordinates geocodeAddress(String address) {
        try {
            String authorization = "Token " + daDataApiKey;

            Map<String, Object> request = Map.of("query", address, "count", 1);
            Map<String, Object> response = dadataFeignClient.geocodeAddress(authorization, "application/json", request);

            if (response != null && response.containsKey("suggestions")) {
                List<Map<String, Object>> suggestions = (List<Map<String, Object>>) response.get("suggestions");
                if (!suggestions.isEmpty()) {
                    Map<String, Object> data = (Map<String, Object>) suggestions.get(0).get("data");
                    if (data != null && data.containsKey("geo_lat") && data.containsKey("geo_lon")) {
                        Double lat = Double.parseDouble(data.get("geo_lat").toString());
                        Double lon = Double.parseDouble(data.get("geo_lon").toString());
                        return new Coordinates(lat, lon);
                    }
                }
            }
            throw new RuntimeException("Координаты адреса не найдены: " + address);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка геокодирования адреса: " + address, e);
        }
    }
}
