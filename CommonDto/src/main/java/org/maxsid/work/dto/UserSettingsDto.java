package org.maxsid.work.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserSettingsDto {

    private Long userId;

    private String homeAddress;

    private String workAddress;

    private String timeZone;

    private String arrivalTimeToWork;
}
