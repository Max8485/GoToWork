package org.maxsid.work.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.maxsid.work.core.entity.UserSchedule;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.repository.UserScheduleRepository;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.service.NotificationService;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.core.service.SchedulerService;
import org.maxsid.work.dto.RouteResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final RouteCalculationService routeCalculationService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000, zone = "Europe/Moscow")
    @SchedulerLock(name = "SchedulerServiceImpl_sendMorningNotifications", lockAtMostFor = "PT30S", lockAtLeastFor = "PT10S")
    @Transactional
    @Override
    public void sendMorningNotifications() { //работает новый шедулер, подумай, может поставить проверку раз в 30 сек, а не раз в 1 мин.

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
        // ✅ Округляем время до минут
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        // ✅ Используем новую колонку
        List<UserSchedule> usersToNotify = userScheduleRepository
                .findByEnabledTrueAndNotificationTime(now);
        log.info("Found {} users to notify", usersToNotify.size());

        for (UserSchedule schedule : usersToNotify) {
            // Проверяем, не отправляли ли уже сегодня
            if (schedule.getLastNotificationDate() != null &&
                    schedule.getLastNotificationDate().equals(today)) {
                log.info("Already sent today for user {}", schedule.getUserId());
                continue;
            }

            // Получаем настройки пользователя
            UserSettings user = userSettingsRepository.findByUserId(schedule.getUserId()).orElse(null);
            if (user == null) continue;

            // Рассчитываем маршрут
            RouteResponse route = routeCalculationService.calculateOptimalRoute(user.getUserId(), false);

            log.info("Recommended departure time: {}", route.getRecommendedDepartureTime());

            // Отправляем уведомление
            notificationService.sendNotification(user.getUserId(), formatRouteMessage(route));

      //      log.info("Calculated notification time: {}, Current time: {}", calculatedNotificationTime, now);

            // Обновляем дату последней отправки
            schedule.setLastNotificationDate(today);
            userScheduleRepository.save(schedule);

            log.info("Notification sent to user {}", schedule.getUserId());
        }



//
//        // Все пользователи с включенными уведомлениями
//        List<UserSchedule> activeSchedules = userScheduleRepository.findByEnabledTrue();
//
//        for (UserSchedule schedule : activeSchedules) {
//            log.info("--- Processing user: {} ---", schedule.getUserId());
//            log.info("Last notification date: {}", schedule.getLastNotificationDate());
//
//            // Получаем настройки пользователя
//            UserSettings user = userSettingsRepository.findByUserId(schedule.getUserId()).orElse(null);
//            if (user == null) continue;
//
//            // Рассчитываем маршрут
//            log.info("Arrival time: {}", user.getArrivalTimeToWork());
//
//            RouteResponse route = routeCalculationService.calculateOptimalRoute(schedule.getUserId(), false); //добавили false
//
//            // Вычисляем время уведомления (выезд - 30 мин)
//            log.info("Recommended departure time: {}", route.getRecommendedDepartureTime());
//
//            LocalTime departureTime = LocalTime.parse(route.getRecommendedDepartureTime());
//            LocalTime calculatedNotificationTime = departureTime.minusMinutes(30);
//
//            log.info("Calculated notification time: {}, Current time: {}", calculatedNotificationTime, now);
//
//            // Если сейчас время уведомления
//            if (calculatedNotificationTime.getHour() == now.getHour() &&
//                    calculatedNotificationTime.getMinute() == now.getMinute()) {
//
//                // Проверяем, не отправляли ли уже сегодня
//                if (schedule.getLastNotificationDate() == null || !schedule.getLastNotificationDate().equals(today)) {
//
//                    // Отправляем уведомление
//                    notificationService.sendNotification(user.getUserId(), formatRouteMessage(route));
//
//                    // Обновляем дату последней отправки
//                    schedule.setLastNotificationDate(today);
//                    userScheduleRepository.save(schedule);
//                }
//            }
//        }
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
