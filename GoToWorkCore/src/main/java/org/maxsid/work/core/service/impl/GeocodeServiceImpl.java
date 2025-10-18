package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.model.DaDataResponse;
import org.maxsid.work.core.service.GeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class GeocodeServiceImpl implements GeocodeService {

    @Value("${app.dadata.api-key}")
    private String dadataApiKey;

    @Value("${app.dadata.url}")
    private String dadataUrl;

    private final RestTemplate restTemplate;

    // Форматтер для правильного парсинга чисел с точкой
    private final DecimalFormat decimalFormat;

    @Autowired
    public GeocodeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        this.decimalFormat = new DecimalFormat("#.######", symbols);
        this.decimalFormat.setParseBigDecimal(true);
    }

    @Override
    public Coordinates geocodeAddress(String address) {
        try {
            log.debug("Geocoding address: {}", address);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Token " + dadataApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", address);
            requestBody.put("count", 1);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<DaDataResponse> response = restTemplate.exchange(
                    dadataUrl, HttpMethod.POST, entity, DaDataResponse.class);

            if (response.getBody() != null && response.getBody().getSuggestions() != null &&
                    !response.getBody().getSuggestions().isEmpty()) {

                var data = response.getBody().getSuggestions().get(0).getData();
                if (data != null && data.getGeo_lat() != null && data.getGeo_lon() != null) {
                    Double lat = parseCoordinate(data.getGeo_lat());
                    Double lon = parseCoordinate(data.getGeo_lon());

                    Coordinates coord = new Coordinates(lat, lon);
                    log.debug("Geocoding successful: {} -> {}", address, coord);
                    return coord;
                }
            }
            throw new RuntimeException("No coordinates found for address: " + address);

        } catch (Exception e) {
            log.error("Geocoding failed for address: {}", address, e);
            throw new RuntimeException("Geocoding failed for address: " + address, e);
        }
    }

    private Double parseCoordinate(String coordinateStr) {
        try {
            // Заменяем запятые на точки для корректного парсинга
            String normalized = coordinateStr.replace(',', '.');
            return decimalFormat.parse(normalized).doubleValue();
        } catch (Exception e) {
            log.error("Failed to parse coordinate: {}", coordinateStr, e);
            throw new RuntimeException("Invalid coordinate format: " + coordinateStr);
        }
    }

//    public String detectTimezone(Coordinates coordinate) { //исправить!
//        return "Europe/Moscow";
//    }
}
