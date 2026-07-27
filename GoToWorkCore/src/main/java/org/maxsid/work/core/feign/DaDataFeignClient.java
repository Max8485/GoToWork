package org.maxsid.work.core.feign;

import org.maxsid.work.dto.DaDataRequest;
import org.maxsid.work.dto.DaDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "dadata", url = "${app.dadata.url}")
public interface DaDataFeignClient {

    @PostMapping(consumes = "application/json")
    DaDataResponse geocodeAddress(
            @RequestHeader("Authorization") String authorization,
            @RequestBody DaDataRequest request
    );
}
