package org.maxsid.work.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserScheduleDto {

    private Long userId;

    private LocalTime notificationTime;

    private boolean enabled;

    private LocalDate lastNotificationDate;
}
