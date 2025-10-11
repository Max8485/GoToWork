package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.UserSettings;
import org.example.repository.UserSettingsRepository;
import org.example.route.RouteRequest;
import org.example.service.UserSettingsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    @Override
    public void saveUserSettings(RouteRequest request) {
        UserSettings settings = userSettingsRepository.findById(request.getId()).orElse(new UserSettings());
        settings.setId(request.getId());
        settings.setHomeAddress(request.getHomeAddress());
        settings.setWorkAddress(request.getWorkAddress());
        settings.setTravelTimeToWork(request.getArrivalTime());
        settings.setTimeZone(request.getTimeZone());
        userSettingsRepository.save(settings);
    }
}
