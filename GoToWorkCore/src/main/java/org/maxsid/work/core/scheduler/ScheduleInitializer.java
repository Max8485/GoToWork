package org.maxsid.work.core.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.repository.UserScheduleRepository;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleInitializer {

    private final UserSettingsRepository userSettingsRepository;
    private final UserScheduleRepository userScheduleRepository;

    @Transactional
    @PostConstruct
    public void initSchedulesForExistingUsers() {
        List<Long> usersIdWithoutSchedules = userSettingsRepository.findUsersWithoutSchedule();

        int created = 0;
        for (Long userId : usersIdWithoutSchedules) {
            UserSchedule userSchedule = UserSchedule.builder()
                    .userId(userId)
                    .enabled(true)
                    .build();
            userScheduleRepository.save(userSchedule);
            created++;
        }
        log.info("Created {} schedules for existing users", created);
    }
}
