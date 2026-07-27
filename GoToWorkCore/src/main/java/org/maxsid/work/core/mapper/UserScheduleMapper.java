package org.maxsid.work.core.mapper;

import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.dto.EnableNotificationsDto;
import org.maxsid.work.dto.UserScheduleDto;
import org.springframework.stereotype.Component;

@Component
public class UserScheduleMapper {

//    public UserScheduleDto mapUserScheduleToUserScheduleDto(UserSchedule userSchedule) {
//        return UserScheduleDto.builder()
//                .userId(userSchedule.getUserId())
//                .enabled(userSchedule.isEnabled())
//                .lastNotificationDate(userSchedule.getLastNotificationDate())
//                .build();
//    }

    public EnableNotificationsDto mapUserScheduleToEnableNotificationsDto(UserSchedule userSchedule) {
        return EnableNotificationsDto.builder()
                .userId(userSchedule.getUserId())
                .enabled(userSchedule.isEnabled())
                .build();
    }
}
