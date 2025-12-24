package org.maxsid.work.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RouteRequest {

    private String homeAddress;

    private String workAddress;

    private String timeZone;

    private String arrivalTime;
}
