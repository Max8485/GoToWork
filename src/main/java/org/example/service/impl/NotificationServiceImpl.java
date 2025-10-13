package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.route.RouteResponse;
import org.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//@RequiredArgsConstructor
//@Service
public class NotificationServiceImpl  {

//    try {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<RouteResponse> entity = new HttpEntity<>(response, headers);
//
//        ResponseEntity<String> telegramResponse = restTemplate.exchange(
//                telegramBotUrl + "/api/notifications",
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//
//        if (telegramResponse.getStatusCode().is2xxSuccessful()) {
//            log.debug("Notification sent successfully to user: {}", response.getUserId());
//        } else {
//            log.error("Failed to send notification to user: {}, status: {}",
//                    response.getUserId(), telegramResponse.getStatusCode());
//        }
//
//    } catch (Exception e) {
//        log.error("Error sending notification to Telegram bot for user {}: {}",
//                response.getUserId(), e.getMessage());
//    }

//    @Value("${app.telegram-bot.url}")
//    private String telegramBotUrl;
//
//    private final RestTemplate restTemplate;
//    @Override
//    public void sendNotification(RouteResponse response) {
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<RouteResponse> entity = new HttpEntity<>(response, headers);
//
//            ResponseEntity<String> telegramResponse = restTemplate.exchange(
//                    telegramBotUrl + "/api/notifications",
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            if (telegramResponse.getStatusCode().is2xxSuccessful()) {
//                System.out.println("Notification sent successfully to user: " + response.getId());
//            } else {
//                System.err.println("Failed to send notification to user: " + response.getId());
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error sending notification to Telegram bot: " + e.getMessage());
//        }
//    }
}
