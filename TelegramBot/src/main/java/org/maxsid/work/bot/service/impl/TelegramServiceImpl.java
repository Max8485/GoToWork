package org.maxsid.work.bot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.bot.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramServiceImpl implements TelegramService{

    private final RestTemplate restTemplate;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public void sendMessage(Long chatId, String text) {
        try {
            // Формируем URL к API Telegram
            String telegramApiUrl = "https://api.telegram.org/bot";
            String url = telegramApiUrl + botToken + "/sendMessage";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chat_id", chatId);
            requestBody.put("text", text);
            requestBody.put("parse_mode", "Markdown");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(url, entity, String.class);

            log.info("Message sent to chat {}: {}", chatId, text);

        } catch (Exception e) {
            log.error("Failed to send message to chat {}: {}", chatId, e.getMessage());
        }
    }
}

