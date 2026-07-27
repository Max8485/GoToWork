package org.maxsid.work.core.mapper;

import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsMapper {
    public UserSettingsDto mapUserSettingsToDto(UserSettings userSettings) {
        return UserSettingsDto.builder()
                .userId(userSettings.getUserId())
                .homeAddress(userSettings.getHomeAddress())
                .workAddress(userSettings.getWorkAddress())
                .arrivalTimeToWork(userSettings.getArrivalTimeToWork())
                .timeZone(userSettings.getTimeZone())
                .build();
    }
}
