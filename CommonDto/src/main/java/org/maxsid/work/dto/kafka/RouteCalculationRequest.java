package org.maxsid.work.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.maxsid.work.dto.RouteRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCalculationRequest {
    private Long chatId;

    private RouteRequest routeRequest;

    private Long timestamp;

    public boolean hasRouteRequest() {
        return routeRequest != null;
    }
}
