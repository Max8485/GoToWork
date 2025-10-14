package org.maxsid.work.core.route;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RouteRequest {
    private Long id;

    private String homeAddress;

    private String workAddress;

    private String timeZone;

    private String arrivalTime; // HH:mm format, время прибытия
}
