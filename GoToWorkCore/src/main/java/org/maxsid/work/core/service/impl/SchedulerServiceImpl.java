package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.service.NotificationService;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.SchedulerService;
import org.maxsid.work.dto.RouteResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SchedulerServiceImpl implements SchedulerService{

    private final UserSettingsRepository userSettingsRepository;
    private final RouteCalculationService routeCalculationService;
    private final NotificationService notificationService;

    //@Scheduled(fixedRate = 60000) // 60 000 мс (1 минута)
    @Scheduled(fixedRate = 60000, zone = "Europe/Moscow") // Не забудь исправить !
    @Override
    public void sendMorningNotifications() {

        List<UserSettings> users = userSettingsRepository.findAll();

        for(UserSettings user : users) {
            RouteResponse route = routeCalculationService.calculateOptimalRoute(user.getUserId());

            String message = formatRouteMessage(route);

            notificationService.sendNotification(user.getUserId(), message);
        }
    }

    private String formatRouteMessage(RouteResponse route) {
        return String.format(
                "🚗 *Ваш маршрут на сегодня*\n\n" +
                        "📍 %s → %s\n" +
                        "⏱ Время в пути: %d минут\n" +
                        "🚀 Рекомендуемое время выезда: %s\n\n" +
                        "Хорошего дня! ☀️",
                route.getHomeAddress(),
                route.getWorkAddress(),
                route.getTravelDurationMinutes(),
                route.getRecommendedDepartureTime()
        );
    }
}
