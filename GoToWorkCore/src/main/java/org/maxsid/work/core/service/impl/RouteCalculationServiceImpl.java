package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.RouteService;
import org.maxsid.work.core.utils.TimeUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RouteCalculationServiceImpl implements RouteCalculationService {

    private final GeocodeService geocodeService;
    private final RouteService routeService;
    private final UserSettingsRepository userSettingsRepository;

    @Override
    public RouteResponse calculateOptimalRoute(Long userId) {
        Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
        if (userSettingsOpt.isEmpty()) {
            throw new IllegalArgumentException("User settings not found for user id: " + userId);
        }

        UserSettings userSettings = userSettingsOpt.get();

        // Проверяем, будний ли день
        if (!TimeUtils.isWeekday()) {
            throw new IllegalStateException("Расчет маршрута доступен только в будние дни");
        }

        // Геокодирование адресов
        Coordinates homeCoords = geocodeService.geocodeAddress(userSettings.getHomeAddress());
        Coordinates workCoords = geocodeService.geocodeAddress(userSettings.getWorkAddress());

        // Расчет времени в пути
        Long travelMinutes = routeService.calculateTravelTimeToWork(homeCoords, workCoords);

        // Расчет времени выезда
        String departureTime = TimeUtils.calculateDepartureTime(
                userSettings.getArrivalTimeToWork(), travelMinutes);

        return new RouteResponse(
                userId,
                userSettings.getHomeAddress(),
                userSettings.getWorkAddress(),
                userSettings.getArrivalTimeToWork(),
                travelMinutes,
                departureTime
        );
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

        return userSettingsRepository.save(userSettings);
    }

    @Override
    public Optional<UserSettings> getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }


//    @Override
//    public RouteResponse calculateOptimalRoute(RouteRequest request) {
//        try {
//            // Геокодирование адресов
//            Coordinates homeCoord = geocodeService.geocodeAddress(request.getHomeAddress());
//            Coordinates workCoord = geocodeService.geocodeAddress(request.getWorkAddress());
//
//            // Определение часового пояса
//            String timezone = request.getTimeZone();
//            if (timezone == null || timezone.trim().isEmpty()) {
//                timezone = geocodeService.detectTimezone(homeCoord);
//            }
//
//            // Расчет времени пути
//            Integer travelTimeSeconds = routeService.calculateTravelTimeToWork(homeCoord, workCoord);
//            int travelTimeMinutes = travelTimeSeconds / 60;
//
//            // Расчет оптимального времени выезда
//            ZoneId zoneId = ZoneId.of(timezone);
//            LocalTime arrivalTime = LocalTime.parse(request.getArrivalTime());
//
//            // Используем завтрашнюю дату для расчета
//            ZonedDateTime arrivalDateTime = ZonedDateTime.now(zoneId)
//                    .plusDays(1)
//                    .with(arrivalTime)
//                    .withSecond(0)
//                    .withNano(0);
//
//            // Вычитаем время пути + 30 минут запаса
//            ZonedDateTime departureDateTime = arrivalDateTime
//                    .minusMinutes(travelTimeMinutes)
//                    .minusMinutes(30);
//
//            // Сохраняем настройки пользователя
//            saveUserSettings(request, timezone);
//
//            String message = buildSuccessMessage(request, travelTimeMinutes, departureDateTime, timezone);
//
//            RouteResponse response = new RouteResponse(
//                    request.getId(),
//                    departureDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
//                    travelTimeMinutes + " мин",
//                    message,
//                    true
//            );
//
//            return response;
//
//        } catch (Exception e) {
//            String errorMessage = buildErrorMessage(e);
//            return new RouteResponse(request.getId(), null, null, errorMessage, false);
//        }
//    }
//
//    private String buildSuccessMessage(RouteRequest request, int travelTimeMinutes,
//                                       ZonedDateTime departureDateTime, String timezone) {
//        return String.format(
//                "🚗 *Расчет маршрута завершен:*\n\n" +
//                        "📍 *От:* %s\n" +
//                        "🏢 *До:* %s\n" +
//                        "⏱️ *Время в пути:* %d мин.\n" +
//                        "⏰ *Желаемое время прибытия:* %s\n" +
//                        "🚀 *Рекомендуемое время выезда:* *%s*\n" +
//                        "🌍 *Часовой пояс:* %s",
//                request.getHomeAddress(),
//                request.getWorkAddress(),
//                travelTimeMinutes,
//                request.getArrivalTime(),
//                departureDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
//                timezone
//        );
//    }
//
//    private String buildErrorMessage(Exception e) {
//        return String.format(
//                "❌ *Ошибка расчета маршрута:*\n\n%s\n\nПроверьте правильность адресов и попробуйте еще раз.",
//                e.getMessage()
//        );
//    }
//
//    private void saveUserSettings(RouteRequest request, String timezone) {
//        UserSettings settings = userSettingsRepository.findById(request.getId())
//                .orElse(new UserSettings());
//        settings.setId(request.getId());
//        settings.setHomeAddress(request.getHomeAddress());
//        settings.setWorkAddress(request.getWorkAddress());
//        settings.setArrivalTimeToWork(request.getArrivalTime());
//        settings.setTimeZone(timezone);
//        userSettingsRepository.save(settings);
//
//        log.debug("Settings saved for user: {}", request.getId());
//    }
//
//    public Optional<UserSettings> getUserSettings(Long userId) {
//        return userSettingsRepository.findById(userId);
//    }
//
//    @Scheduled(cron = "0 0 6 * * MON-FRI") // Каждый будний день в 6 утра
//    public void sendDailyNotifications() {
//        log.info("Starting daily notifications calculation");
//
//        List<UserSettings> allSettings = userSettingsRepository.findAll();
//        log.info("Found {} users for notifications", allSettings.size());
//
//        for (UserSettings settings : allSettings) {
//            try {
//                ZoneId userZone = ZoneId.of(settings.getTimeZone());
//                ZonedDateTime userNow = ZonedDateTime.now(userZone);
//
//                // Проверяем, что сейчас будний день по времени пользователя
//                DayOfWeek userDayOfWeek = userNow.getDayOfWeek();
//                if (userDayOfWeek != DayOfWeek.SATURDAY && userDayOfWeek != DayOfWeek.SUNDAY) {
//
//                    RouteRequest request = new RouteRequest(
//                            settings.getId(),
//                            settings.getHomeAddress(),
//                            settings.getWorkAddress(),
//                            settings.getArrivalTimeToWork(),
//                            settings.getTimeZone()
//                    );
//
//                    RouteResponse response = calculateOptimalRoute(request);
//
//                    if (response.isSuccess()) {
//                        String dailyMessage = String.format(
//                                "📅 *Ежедневное уведомление на %s*\n\n%s",
//                                userNow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
//                                response.getMessage()
//                        );
//                        response.setMessage(dailyMessage);
//
////                        notificationService.sendNotification(response);
////                        log.info("Notification sent to user: {}", settings.getId());
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Error sending notification to user {}: {}", settings.getId(), e.getMessage());
//            }
//        }
//
//        log.info("Daily notifications completed");
//    }
}