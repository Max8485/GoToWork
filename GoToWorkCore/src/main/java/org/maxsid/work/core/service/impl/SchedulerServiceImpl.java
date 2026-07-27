package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.repository.UserScheduleRepository;
import org.maxsid.work.core.service.NotificationService;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.SchedulerService;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserNotificationData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final UserScheduleRepository userScheduleRepository;
    private final RouteCalculationService routeCalculationService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 30000, zone = "Europe/Moscow")
    @SchedulerLock(name = "SchedulerServiceImpl_sendMorningNotifications", lockAtMostFor = "PT30S", lockAtLeastFor = "PT10S")
    @Transactional
    @Override
    public void sendMorningNotifications() {

        // проблема:
        // TelegramBot получает сообщение из Kafka каждый раз, когда GoToWorkCore рассчитывает маршрут.
        // А GoToWorkCore рассчитывает маршрут каждую минуту в шедулере.
        // Отправлять в Kafka только при ручном запросе

        //решение:
        // в Telegram будут приходить только:
        //✅ По вашей команде /calculate
        //✅ От шедулера (за 30 мин до выезда) - уже через notificationService
        //Дублей не будет!


        log.info("=== METHOD sendMorningNotifications CALLED ===");
        // Округляем время до минут
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        List<UserNotificationData> usersToNotify = userScheduleRepository.findUsersToNotify(now);
        log.info("Found {} users to notify", usersToNotify.size());

        List<UserSchedule> toUpdate = new ArrayList<>();  // Создаём список

        for (UserNotificationData data : usersToNotify) {
            // Проверяем, не отправляли ли уже сегодня
            if (data.getLastNotificationDate() != null &&
                    data.getLastNotificationDate().equals(today)) {
                log.info("Already sent today for user {}", data.getUserId());
                continue;
            }

            // Получаем настройки пользователя
            if (data.getUserId() == null) continue;

            // Рассчитываем маршрут
            RouteResponse route = routeCalculationService.calculateOptimalRoute(data.getUserId(), false);

            log.info("Recommended departure time: {}", route.getRecommendedDepartureTime());

            // Отправляем уведомление
            notificationService.sendNotification(data.getUserId(), formatRouteMessage(route));

            // Создаём объект для обновления
            UserSchedule schedule = new UserSchedule();
            schedule.setUserId(data.getUserId());
            schedule.setEnabled(data.isEnabled());
            schedule.setLastNotificationDate(today);
            schedule.setNotificationTime(data.getNotificationTime());
            toUpdate.add(schedule);
        }

        //  1 запрос на все обновления
        if (!toUpdate.isEmpty()) {
            userScheduleRepository.saveAll(toUpdate);
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
