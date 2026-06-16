package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.repository.UserScheduleRepository;
import org.maxsid.work.core.service.UserScheduleService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserScheduleServiceImpl implements UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;

    @Override
    public UserSchedule enableNotifications(Long userId, boolean enabled) {
        UserSchedule schedule = userScheduleRepository.findByUserId(userId).orElseGet(() -> UserSchedule.builder()
                .userId(userId)
                .enabled(enabled)
                .build());

        schedule.setEnabled(enabled);
       return userScheduleRepository.save(schedule);
    }
}
