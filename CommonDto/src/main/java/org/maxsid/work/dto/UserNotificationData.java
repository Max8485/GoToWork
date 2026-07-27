package org.maxsid.work.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class UserNotificationData {
    public UserNotificationData(Long userId, String homeAddress, String workAddress,
                                String arrivalTimeToWork, LocalTime notificationTime, LocalDate lastNotificationDate, boolean enabled) {
        this.userId = userId;
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.arrivalTimeToWork = arrivalTimeToWork;
        this.notificationTime = notificationTime;
        this.lastNotificationDate = lastNotificationDate;
        this.enabled = enabled;
    }

    private Long userId;
    private String homeAddress;
    private String workAddress;
    private String arrivalTimeToWork;
    private LocalTime notificationTime;
    private LocalDate lastNotificationDate;
    private boolean enabled;
}
