package org.maxsid.work.bot.kafka.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.bot.controller.TransportBot;
import org.maxsid.work.bot.kafka.service.KafkaConsumerService;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final TransportBot transportBot;

    @Override
    @KafkaListener(topics = "user-settings-topic", groupId = "telegram-bot-service")
    public void consumeUserSettingsFromCore(
            @Payload UserSettingsDto userSettingsDto,
            @Header(KafkaHeaders.RECEIVED_KEY) String userId) {

        log.info(">>> Received user settings for user: {}", userId);

        String notification = String.format("""
                        ✅ Настройки успешно синхронизированы!

                        🏠 Домашний адрес: %s
                        🏢 Рабочий адрес: %s
                        ⏰ Время прибытия: %s

                        Теперь можете использовать команду /calculate
                        """,
                userSettingsDto.getHomeAddress(),
                userSettingsDto.getWorkAddress(),
                userSettingsDto.getArrivalTimeToWork()
        );

        sendTelegramNotification(Long.parseLong(userId), notification);
    }

    @Override
    @KafkaListener(topics = "route-calculated-topic", groupId = "telegram-bot-service")
    public void consumeRouteCalculatedFromCore(
            @Payload RouteResponse routeResponse,
            @Header(KafkaHeaders.RECEIVED_KEY) String userId) {

        log.info(">>> Received route calculation for userId: {}", userId);

        String notification = String.format("""
                🚗 Маршрут рассчитан системой:

                %s
                                    
                Можете проверить настройки командой /settings
                """, routeResponse.getMessage());

        sendTelegramNotification(Long.parseLong(userId), notification);
    }

    private void sendTelegramNotification(Long userId, String message) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId.toString());
            sendMessage.setText(message);
            transportBot.execute(sendMessage);
            log.info(">>> Notification sent to user {} via Telegram", userId);
        } catch (TelegramApiException e) {
            log.error(">>> Failed to send Telegram notification to user {}", userId, e);
        }
    }
}
