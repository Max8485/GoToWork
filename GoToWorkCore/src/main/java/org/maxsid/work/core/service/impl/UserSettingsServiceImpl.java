package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.route.RouteRequest;
import org.maxsid.work.core.service.UserSettingsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public void saveUserSettingsForBot(RouteRequest request) {
        UserSettings settings = userSettingsRepository.findById(request.getId())
                .orElse(new UserSettings());
        settings.setId(request.getId());
        settings.setHomeAddress(request.getHomeAddress());
        settings.setWorkAddress(request.getWorkAddress());
        settings.setArrivalTimeToWork(request.getArrivalTime());
        settings.setTimeZone(request.getTimeZone());
        userSettingsRepository.save(settings);

        log.debug("Settings saved for user: {}", request.getId());
    }
}
