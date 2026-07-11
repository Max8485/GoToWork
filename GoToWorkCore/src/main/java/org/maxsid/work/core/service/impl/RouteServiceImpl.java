package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.exceptions.RouteCalculationException;
import org.maxsid.work.core.feign.GraphHopperFeignClient;
import org.maxsid.work.core.model.Vehicle;
import org.maxsid.work.core.service.RouteService;
import org.maxsid.work.dto.GraphHopperResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RouteServiceImpl implements RouteService {

    @Value("${app.graphhooper.api-key}")
    private String graphApiKey;

    private final GraphHopperFeignClient graphHopperFeignClient;

    public Long calculateTravelTimeToWork(Coordinates from, Coordinates to) { //работает с feign client
        try {
            List<String> points = List.of(formatPoint(from), formatPoint(to));

            GraphHopperResponse response = graphHopperFeignClient.getRoute(
                    points,
                    Vehicle.CAR.getValue(),
                    graphApiKey,
                    false
            );
            log.info("Full response: {}", response);

            if (response == null || response.getPaths() == null || response.getPaths().isEmpty()) {
                throw new RouteCalculationException("No paths found");
            }

            GraphHopperResponse.Path path = response.getPaths().get(0);
            Long timeMs = path.getTime();

            if (timeMs == null || timeMs <= 0) {
                throw new RouteCalculationException("Invalid time");
            }

            return timeMs / 1000 / 60;

        } catch (Exception e) {
            log.error("Error calculating route from {} to {}: {}", from, to, e.getMessage(), e);
            throw new RouteCalculationException("Failed to calculate route", e);
        }
    }

    private String formatPoint(Coordinates coords) {
        return coords.getLat() + "," + coords.getLon();
    }
}


//Из того что не нравится - обилие магических констант и потенциальных NPE
//все таки лучше завести дто, мб использовать какой-то другой http клиент вроде feign, должно складнее получится

//    public Long calculateTravelTimeToWork(Coordinates from, Coordinates to) { //попробуй заменить на feign client
//        String url = buildUrl(from, to);
//
//        try {
//            ResponseEntity<GraphHopperResponse> response = restTemplate.getForEntity(url, GraphHopperResponse.class);
//            GraphHopperResponse body = response.getBody();
//
//            if (body == null || body.getPaths() == null || body.getPaths().isEmpty()) {
//                log.warn("No paths found in GraphHopper response for route from {} to {}", from, to);
//                throw new RouteCalculationException("No paths found for route");
//            }
//
//            GraphHopperResponse.Path path = body.getPaths().get(0);
//            Long timeMs = path.getTime();
//
//            if (timeMs == null || timeMs <= 0) {
//                log.warn("Invalid time value: {} for route from {} to {}", timeMs, from, to);
//                throw new RouteCalculationException("Invalid time value in response");
//            }
//
//            return timeMs / 1000 / 60;
//
//        } catch (Exception e) {
//            log.error("Error calculating route from {} to {}: {}", from, to, e.getMessage(), e);
//            throw new RouteCalculationException("Failed to calculate route", e);
//        }
//    }
//
//    private String buildUrl(Coordinates from, Coordinates to) {
//        return UriComponentsBuilder.fromHttpUrl(graphUrl)
//                .queryParam(POINT, formatPoint(from))
//                .queryParam(POINT, formatPoint(to))
//                .queryParam(VEHICLE, Vehicle.CAR.getValue())
//                .queryParam(KEY, graphApiKey)
//                .queryParam(CALC_POINTS, CALC_POINTS_DEFAULT)
//                .toUriString();
//    }
//
//    private String formatPoint(Coordinates coords) {
//        return coords.getLat() + "," + coords.getLon();
//    }
//}