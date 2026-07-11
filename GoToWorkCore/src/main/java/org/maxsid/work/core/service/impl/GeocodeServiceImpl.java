package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.feign.DaDataFeignClient;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.MetricsService;
import org.maxsid.work.dto.DaDataRequest;
import org.maxsid.work.dto.DaDataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
@Service
public class GeocodeServiceImpl implements GeocodeService {

    @Value("${app.dadata.api-key}")
    private String dadataApiKey;

    private final MetricsService metricsService;

    private final DaDataFeignClient daDataFeignClient;
    private final DecimalFormat decimalFormat = initDecimalFormat();

    // Форматтер для правильного парсинга чисел с точкой
    private DecimalFormat initDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.######", symbols);
        df.setParseBigDecimal(true);
        return df;
    }

    @Override
    public Coordinates geocodeAddress(String address) {  //работает с feign client
        try {
            log.debug("Geocoding address: {}", address);

            DaDataResponse response = daDataFeignClient.geocodeAddress(
                    "Token " + dadataApiKey,
                    new DaDataRequest(address, 1));

            if (response != null && response.getSuggestions() != null &&
                    !response.getSuggestions().isEmpty()) {

                var addressData = response.getSuggestions().get(0).getAddressData();
                if (addressData != null && addressData.getGeoLat() != null && addressData.getGeoLon() != null) {
                    Double lat = parseCoordinate(addressData.getGeoLat());
                    Double lon = parseCoordinate(addressData.getGeoLon());

                    Coordinates coord = new Coordinates(lat, lon);
                    log.debug("Geocoding successful: {} -> {}", address, coord);

                    metricsService.updateSyncStatus(true); // Успешное геокодирование — метрика OK
                    return coord;
                }
            }

            metricsService.updateSyncStatus(false);  // Нет координат — метрика ERROR
            throw new RuntimeException("No coordinates found for address: " + address);

        } catch (Exception e) {
            log.error("Geocoding failed for address: {}", address, e);

            metricsService.updateSyncStatus(false);  // Ошибка — метрика ERROR
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




//    @Override
//    public Coordinates geocodeAddress(String address) {
//        try {
//            log.debug("Geocoding address: {}", address);
//
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
//                    Double lat = parseCoordinate(data.getGeo_lat());
//                    Double lon = parseCoordinate(data.getGeo_lon());
//
//                    Coordinates coord = new Coordinates(lat, lon);
//                    log.debug("Geocoding successful: {} -> {}", address, coord);
//
//                    // ✅ Успешное геокодирование — метрика OK
//                    metricsService.updateSyncStatus(true);
//
//                    return coord;
//                }
//            }
//
//            // ❌ Нет координат — метрика ERROR
//            metricsService.updateSyncStatus(false);
//            throw new RuntimeException("No coordinates found for address: " + address);
//
//        } catch (Exception e) {
//            log.error("Geocoding failed for address: {}", address, e);
//
//            // ❌ Ошибка — метрика ERROR
//            metricsService.updateSyncStatus(false);
//            throw new RuntimeException("Geocoding failed for address: " + address, e);
//        }
//    }

}
