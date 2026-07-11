package org.maxsid.work.core.feign;

import org.maxsid.work.dto.GraphHopperResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "graphhopper", url = "${app.graphhooper.url}")
public interface GraphHopperFeignClient {
    @GetMapping
    GraphHopperResponse getRoute(
            @RequestParam("point")List<String> points,
            @RequestParam("vehicle") String vehicle,
            @RequestParam("key") String apiKey,
            @RequestParam("calc_points") boolean calcPoints
    );
}

