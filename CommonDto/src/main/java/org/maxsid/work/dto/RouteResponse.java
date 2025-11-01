package org.maxsid.work.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RouteResponse {

    private Long userId;

    private String homeAddress;

    private String workAddress;

    private String arrivalTime;

    private Long travelDurationMinutes;  //продолжительность пути

    private String recommendedDepartureTime; //время отправления

    private String message;

    public RouteResponse(Long userId, String homeAddress, String workAddress,
                         String arrivalTime, Long travelDurationMinutes,
                         String recommendedDepartureTime) {
        this.userId = userId;
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.arrivalTime = arrivalTime;
        this.travelDurationMinutes = travelDurationMinutes;
        this.recommendedDepartureTime = recommendedDepartureTime;
        this.message = generateMessage();
    }

    private String generateMessage() {
        return String.format(
                "Маршрут от %s до %s займет %d минут. Рекомендуемое время выезда: %s",
                homeAddress, workAddress, travelDurationMinutes, recommendedDepartureTime
        );
    }
}
