package org.maxsid.work.core.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserScheduleRepository;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer {

    private final UserSettingsRepository userSettingsRepository;
    private final UserScheduleRepository userScheduleRepository;

    @PostConstruct
    public void initSchedulesForExistingUsers() {
        // Находим всех пользователей с настройками, но без расписания
        List<UserSettings> usersWithoutSchedules = userSettingsRepository.findAll().stream()
                .filter(user -> !userScheduleRepository.existsById(user.getUserId()))
                .toList();

        for (UserSettings user : usersWithoutSchedules) {
            UserSchedule userSchedule = UserSchedule.builder()
                    .userId(user.getUserId())
                    .enabled(true)
                    .build();

            userScheduleRepository.save(userSchedule);
        }
    }
}
