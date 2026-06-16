package org.maxsid.work.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "user_schedules")
public class UserSchedule {

    @Id
    @Column(name = "user_id")
    private Long userId;  //

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "last_notification_date")
    private LocalDate lastNotificationDate;
}
