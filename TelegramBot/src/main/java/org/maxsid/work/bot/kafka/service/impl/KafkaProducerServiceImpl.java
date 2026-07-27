package org.maxsid.work.bot.kafka.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.bot.kafka.service.KafkaProducerService;
import org.maxsid.work.dto.RouteRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendUserSettingsToCore(Long chatId, RouteRequest request) {
        try {
            String correlationId = UUID.randomUUID().toString();

            Message<RouteRequest> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, "user-settings-from-bot-topic")
                    .setHeader(KafkaHeaders.KEY, chatId.toString())
                    .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info(">>> [TelegramBot] Пользовательские настройки успешно отправлены в Core для chatId: {}", chatId);
                        } else {
                            log.error(">>> [TelegramBot] Failed to send user settings for chatId: {}", chatId, ex);
                        }
                    });

        } catch (Exception e) {
            log.error(">>> [TelegramBot] Error sending user settings for chatId: {}", chatId, e);
        }
    }

    @Override
    public void sendRouteCalculationRequestToCore(Long chatId, RouteRequest routeRequest) {
        try {
            String correlationId = UUID.randomUUID().toString();

            Message<RouteRequest> message = MessageBuilder
                    .withPayload(routeRequest)
                    .setHeader(KafkaHeaders.TOPIC, "route-calculated-from-bot-topic")
                    .setHeader(KafkaHeaders.KEY, chatId.toString())
                    .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info(">>> [TelegramBot] Запрос на расчет маршрута успешно отправлен в Core для chatId:: {}", chatId);
                        } else {
                            log.error(">>> [TelegramBot] Failed to send route calculation request for chatId: {}", chatId, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("> [TelegramBot] Error sending route calculation request for chatId: {}", chatId, e);
        }
    }
}

