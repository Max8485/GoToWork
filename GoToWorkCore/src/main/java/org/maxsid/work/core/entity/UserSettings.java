package org.maxsid.work.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "work_address")
    private String workAddress;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "arrival_time_to_work")
    private String arrivalTimeToWork;

    public UserSettings(Long userId, String homeAddress, String workAddress, String timeZone, String arrivalTimeToWork) {
        this.userId = userId;
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
        this.timeZone = timeZone;
        this.arrivalTimeToWork = arrivalTimeToWork;
    }
}
