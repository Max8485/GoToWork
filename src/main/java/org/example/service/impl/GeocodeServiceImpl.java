package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coordinates.Coordinates;
import org.example.feign.DadataFeignClient;
import org.example.model.DaDataResponse;
import org.example.service.GeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
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

            if (response.getBody() != null &&
                    response.getBody().getSuggestions() != null &&
                    !response.getBody().getSuggestions().isEmpty()) {

                var data = response.getBody().getSuggestions().get(0).getData();
                if (data != null && data.getGeo_lat() != null && data.getGeo_lon() != null) {
                    // Правильный парсинг координат с учетом локали
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

//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Token " + dadataApiKey);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("query", address);
//            requestBody.put("count", 1);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//            ResponseEntity<DaDataResponse> response = restTemplate.exchange(
//                    dadataUrl, HttpMethod.POST, entity, DaDataResponse.class);
//
//            if (response.getBody() != null && response.getBody().getSuggestions() != null &&
//                    !response.getBody().getSuggestions().isEmpty()) {
//
//                var data = response.getBody().getSuggestions().get(0).getData();
//                if (data != null && data.getGeo_lat() != null && data.getGeo_lon() != null) {
//                    return new Coordinates(
//                            Double.parseDouble(data.getGeo_lat()),
//                            Double.parseDouble(data.getGeo_lon())
//                    );
//                }
//            }
//            throw new RuntimeException("No coordinates found for address: " + address);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Geocoding failed for address: " + address, e);
//        }
//    }

    public String detectTimezone(Coordinates coordinate) {
        // Для простоты используем московский часовой пояс
        // В реальном приложении можно использовать Timezone API
        return "Europe/Moscow";
    }
}
