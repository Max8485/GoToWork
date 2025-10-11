package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "dadata-client", url = "${app.dadata.url}")
public interface DadataFeignClient {

    @PostMapping //написать адрес api/v1/geocode ?
    Map<String, Object> geocodeAddress(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Content-Type") String contentType,
            @RequestBody Map<String, Object> request
    );
}
