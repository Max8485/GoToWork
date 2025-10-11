package org.example.route;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RouteResponse {

    private Long id;

    private String departureTime; //время отправления

    private String travelDuration; //продолжительность пути

    private String message;

    private boolean success; //успех
}
