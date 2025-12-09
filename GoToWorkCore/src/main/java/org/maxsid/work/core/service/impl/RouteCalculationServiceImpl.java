package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;

import org.maxsid.work.core.kafka.service.KafkaProducerService;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.RouteService;
import org.maxsid.work.core.utils.TimeUtils;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RouteCalculationServiceImpl implements RouteCalculationService {

    private final GeocodeService geocodeService;
    private final RouteService routeService;
    private final UserSettingsRepository userSettingsRepository;
    private final KafkaProducerService kafkaProducerService;


    @Override
    public RouteResponse calculateOptimalRoute(Long userId) {
        Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
        if (userSettingsOpt.isEmpty()) {
            throw new IllegalArgumentException("User settings not found for user id: " + userId);
        }

        UserSettings userSettings = userSettingsOpt.get();

//        // Проверяем, будний ли день, работает!
//        if (!TimeUtils.isWeekday()) {
//            log.info("Расчет маршрута доступен только в будние дни");
//            throw new IllegalStateException("Расчет маршрута доступен только в будние дни");
//        }

        // Геокодирование адресов
        Coordinates homeCoords = geocodeService.geocodeAddress(userSettings.getHomeAddress());
        Coordinates workCoords = geocodeService.geocodeAddress(userSettings.getWorkAddress());

        // Расчет времени в пути
        Long travelMinutes = routeService.calculateTravelTimeToWork(homeCoords, workCoords);

        // Расчет времени выезда
        String departureTime = TimeUtils.calculateDepartureTime(
                userSettings.getArrivalTimeToWork(), travelMinutes);

//        return new RouteResponse(
//                userId,
//                userSettings.getHomeAddress(),
//                userSettings.getWorkAddress(),
//                userSettings.getArrivalTimeToWork(),
//                travelMinutes,
//                departureTime
//        );

        RouteResponse response = new RouteResponse(
                userId,
                userSettings.getHomeAddress(),
                userSettings.getWorkAddress(),
                userSettings.getArrivalTimeToWork(),
                travelMinutes,
                departureTime
        );
        // Отправка события в Kafka
        kafkaProducerService.sendRouteCalculatedEvent(userId, response);

        return response;
    }

    @Override
    public UserSettings saveUserSettings(Long userId, RouteRequest request) {
        // Проверяем существующие настройки
        Optional<UserSettings> existingSettings = userSettingsRepository.findByUserId(userId);

        UserSettings userSettings;
        if (existingSettings.isPresent()) {
            // Обновляем существующие настройки
            userSettings = existingSettings.get();
            userSettings.setHomeAddress(request.getHomeAddress());
            userSettings.setWorkAddress(request.getWorkAddress());
            userSettings.setTimeZone(request.getTimeZone());
            userSettings.setArrivalTimeToWork(request.getArrivalTime());
        } else {
            // Создаем новые настройки
            userSettings = new UserSettings(
                    userId,
                    request.getHomeAddress(),
                    request.getWorkAddress(),
                    request.getTimeZone() != null ? request.getTimeZone() : "Europe/Moscow",
                    request.getArrivalTime()
            );
        }

//        return userSettingsRepository.save(userSettings);

        UserSettings savedSettings = userSettingsRepository.save(userSettings);

        // Отправка события в Kafka
        UserSettingsDto dto = UserSettingsDto.builder()
                .userId(savedSettings.getUserId())
                .homeAddress(savedSettings.getHomeAddress())
                .workAddress(savedSettings.getWorkAddress())
                .timeZone(savedSettings.getTimeZone())
                .arrivalTimeToWork(savedSettings.getArrivalTimeToWork())
                .build();

        kafkaProducerService.sendUserSettingsSavedEvent(userId, dto);

        return savedSettings;
    }

    @Override
    public Optional<UserSettings> getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }
}