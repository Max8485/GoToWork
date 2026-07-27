package org.maxsid.work.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.maxsid.work.bot.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("api/telegram")
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final TelegramService telegramService;

    //TelegramBot принимает данные от GoToWorkCore
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, Object> request) {

        try {
            Long chatId = Long.valueOf(request.get("chatId").toString());
            String message = request.get("message").toString();

            log.info("Received notification request for chatId: {}", chatId);

            telegramService.sendMessage(chatId, message);

            return ResponseEntity.ok("Notification sent successfully");

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed: " + e.getMessage());
        }
    }
}
