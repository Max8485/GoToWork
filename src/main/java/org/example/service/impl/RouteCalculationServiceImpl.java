package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.coordinates.Coordinates;
import org.example.entity.UserSettings;
import org.example.repository.UserSettingsRepository;
import org.example.route.RouteRequest;
import org.example.route.RouteResponse;
import org.example.service.GeocodeService;
import org.example.service.RouteCalculationService;
import org.example.service.RouteService;
import org.example.service.UserSettingsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RouteCalculationServiceImpl implements RouteCalculationService {

    private final GeocodeService geocodeService;
    private final RouteService routeService;
    private final UserSettingsRepository userSettingsRepository;
    private final UserSettingsService userSettingsService;

    @Override
    public RouteResponse calculateOptimalRoute(RouteRequest request) { //переиминовать в calculateOptimalRoute
        try {
            // Геокодирование адресов
            Coordinates homeCoordinates = geocodeService.geocodeAddress(request.getHomeAddress());
            Coordinates workCoordinates = geocodeService.geocodeAddress(request.getWorkAddress());

            // Расчет времени пути
            Integer travelTimeInSeconds = routeService.calculateTravelTimeToWork(homeCoordinates, workCoordinates);

            int travelTimeInMinutes = travelTimeInSeconds / 60;

            // Расчет оптимального времени выезда
            ZoneId zoneId = ZoneId.of(request.getTimeZone());
            LocalTime arrivalTime = LocalTime.parse(request.getArrivalTime());

            // Используем завтрашнюю дату для расчета
            ZonedDateTime arrivalDateTime = ZonedDateTime.now(zoneId)
                    .plusDays(1)
                    .with(arrivalTime)
                    .withSecond(0)
                    .withNano(0);

            // Вычитаем время пути + 30 минут запаса
            ZonedDateTime departureDateTime = arrivalDateTime
                    .minusMinutes(travelTimeInMinutes)
                    .minusMinutes(30);

            // Сохраняем настройки пользователя
            userSettingsService.saveUserSettings(request);

            String message = String.format(
                    "🚗 *Расчет маршрута завершен:*\n\n" +
                            "📍 *От:* %s\n" +
                            "🏢 *До:* %s\n" +
                            "⏱️ *Время в пути:* %d мин.\n" +
                            "⏰ *Желаемое время прибытия:* %s\n" +
                            "🚀 *Рекомендуемое время выезда:* *%s*",
                    request.getHomeAddress(),
                    request.getWorkAddress(),
                    travelTimeInMinutes,
                    request.getArrivalTime(),
                    departureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            return new RouteResponse(request.getId(),
                    departureDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    travelTimeInMinutes + "мин",
                    message,
                    true);

        } catch (Exception e) {
            String errorMessage = "❌ *Ошибка расчета маршрута:*\n\n" + e.getMessage();
            return new RouteResponse(request.getId(), null, null, errorMessage, false);
        }
    }

//    private void saveUserSettings(RouteRequest request) {
//        UserSettings settings = userSettingsRepository.findById(request.getId()).orElse(new UserSettings());
//        settings.setId(request.getId());
//        settings.setHomeAddress(request.getHomeAddress());
//        settings.setWorkAddress(request.getWorkAddress());
//        settings.setTravelTimeToWork(request.getArrivalTime());
//        settings.setTimeZone(request.getTimeZone());
//        userSettingsRepository.save(settings);
//    }


    @Scheduled(cron = "0 0 8 * * MON-FRI")  // Каждый будний день в 8 утра
    @Override
    public void calculateDailyRoutes() { //убрать из сервиса ?
        List<UserSettings> allSettings = userSettingsRepository.findAll();

        for (UserSettings settings : allSettings) {
            try {
                RouteRequest request = new RouteRequest(
                        settings.getId(),
                        settings.getHomeAddress(),
                        settings.getWorkAddress(),
                        settings.getTravelTimeToWork(),
                        settings.getTimeZone());

                RouteResponse response = calculateOptimalRoute(request);

                // Здесь будет вызов сервиса уведомлений через Feign Client
                System.out.println("Расчитать маршрут для пользователя " + settings.getId() + ": " + response.getMessage());

            } catch (Exception e) {
                System.err.println("Ошибра при расчете маршрута для пользователя " + settings.getId() + ": " + e.getMessage());
            }
        }
    }
}
