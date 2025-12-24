package org.maxsid.work.core.kafka.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.core.kafka.service.KafkaProducerService;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserSettingsSavedEvent(Long userId, UserSettingsDto userSettingsDto) {
        try {
            String correlationId = UUID.randomUUID().toString();

            Message<UserSettingsDto> message = MessageBuilder
                    .withPayload(userSettingsDto)
                    .setHeader(KafkaHeaders.TOPIC, "user-settings-topic")
                    .setHeader(KafkaHeaders.KEY, userId.toString())
                    .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("[Correlation: {}] Настройки пользователя успешно отправлены{}", correlationId, userId);
                        } else {
                            log.error("[Correlation: {}] Failed for user {}", correlationId, userId, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending user settings for user {}", userId, e);
        }
    }

    @Override
    public void sendRouteCalculatedEvent(Long userId, RouteResponse routeResponse) {
        try {
            String correlationId = UUID.randomUUID().toString();

            Message<RouteResponse> message = MessageBuilder
                    .withPayload(routeResponse)
                    .setHeader(KafkaHeaders.TOPIC, "route-calculated-topic")
                    .setHeader(KafkaHeaders.KEY, userId.toString())
                    .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("[Correlation: {}] Расчет маршрута для пользователя успешно отправлен{}", correlationId, userId);
                        } else {
                            log.error("[Correlation: {}] Failed for route calculate{}", correlationId, userId, ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error when sending the route calculation{}", userId, e);
        }
    }
}
