package org.maxsid.work.core.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RouteRequest {
//    private Long id;

    private String homeAddress;

    private String workAddress;

    private String timeZone;

    private String arrivalTime;
}
