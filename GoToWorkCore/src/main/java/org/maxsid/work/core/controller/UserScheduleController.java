package org.maxsid.work.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.mapper.UserScheduleMapper;
import org.maxsid.work.core.service.UserScheduleService;
import org.maxsid.work.dto.EnableNotificationsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class UserScheduleController {

    private final UserScheduleService userScheduleService;
    private final UserScheduleMapper userScheduleMapper;

    @PostMapping("/users/{userId}/notifications")
    public ResponseEntity<EnableNotificationsDto> enableNotifications(@PathVariable Long userId,
                                                                      @RequestParam boolean enabled) {

        UserSchedule userSchedule = userScheduleService.enableNotifications(userId, enabled);

        EnableNotificationsDto dto = userScheduleMapper.mapUserScheduleToEnableNotificationsDto(userSchedule);
        log.info("Notifications for user {} set to {}", userId, enabled);
        return ResponseEntity.ok(dto);
    }
}
