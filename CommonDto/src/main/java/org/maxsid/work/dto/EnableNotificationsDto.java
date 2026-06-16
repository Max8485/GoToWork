package org.maxsid.work.dto;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EnableNotificationsDto {

    private Long userId;

    private boolean enabled;
}
