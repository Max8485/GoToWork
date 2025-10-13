package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@FeignClient(name = "twoGIS-client", url = "${app.twogis.url}") //проверить url
public interface TwoGisFeignClient {

//    @GetMapping("/get_route") //исправить на api/v1/route ?
//    Map<String, Object> calculateRoute(
//            @RequestHeader("Authorization") String authorization,
//            @RequestParam("points") String points,
//            @RequestParam("type") String type,
//            @RequestParam("traffic") Boolean traffic);
}
