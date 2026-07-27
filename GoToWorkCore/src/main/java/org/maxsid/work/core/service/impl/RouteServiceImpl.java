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

    public Long calculateTravelTimeToWork(Coordinates from, Coordinates to) {
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