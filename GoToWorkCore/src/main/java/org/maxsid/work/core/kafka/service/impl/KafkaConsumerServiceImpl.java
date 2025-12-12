package org.maxsid.work.core.kafka.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.kafka.service.KafkaProducerService;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.service.RouteCalculationService;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.kafka.RouteCalculationRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl {

    private final RouteCalculationService routeCalculationService;
    private final UserSettingsRepository userSettingsRepository;
    /**
     * Получение настроек пользователя от TelegramBot
     */
    @KafkaListener(topics = "user-settings-from-bot-topic", groupId = "go-to-work-core-service")
    public void consumeUserSettingsFromBot(@Payload RouteRequest routeRequest,
                                           @Header(KafkaHeaders.RECEIVED_KEY) String chatId,
                                           @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        try {
            Long userId = Long.parseLong(chatId);
            log.info(">>> [GoToWorkCore] Processing user settings for userId: {}, correlation: {}",
                    userId, correlationId);

            // Сохраняем настройки
            routeCalculationService.saveUserSettings(userId, routeRequest);

            log.info(">>> [GoToWorkCore] Настройки пользователя получены для userId: {}", userId);

        } catch (Exception e) {
            log.error(">>> [GoToWorkCore] Error processing user settings for chatId: {}", chatId, e);
        }
    }

    /**
     * Получение запроса на расчет маршрута от TelegramBot
     */
    @KafkaListener(topics = "route-calculated-from-bot-topic", groupId = "go-to-work-core-service")
    public void consumeRouteCalculationRequestFromBot(@Payload RouteRequest routeRequest,
                                                      @Header(KafkaHeaders.RECEIVED_KEY) String chatId,
                                                      @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        try {
            Long userId = Long.parseLong(chatId);
            log.info(">>> [GoToWorkCore] Received route calculation request from bot for userId: {}, correlation: {}",
                    userId, correlationId);

            // Проверяем, есть ли настройки у пользователя
            Optional<UserSettings> userSettingsByUserId = userSettingsRepository.findByUserId(userId);
            if (userSettingsByUserId.isEmpty()) {
                log.warn(">>> [GoToWorkCore] No user settings found for userId: {}", userId);
                return;
            }

            log.info(" Запрос на расчет маршрута получен: {}", routeRequest);

            // Выполняем расчет маршрута
            routeCalculationService.calculateOptimalRoute(userId);
            log.info(">>> [GoToWorkCore] Маршрут успешно рассчитан для userId: {}", userId);

        } catch (Exception e) {
            log.error(">>> [GoToWorkCore] Error processing route calculation request for chatId: {}",
                    chatId, e);
        }
    }
}