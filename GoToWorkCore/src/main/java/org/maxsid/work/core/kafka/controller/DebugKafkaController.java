package org.maxsid.work.core.kafka.controller;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.kafka.service.KafkaProducerService;
import org.maxsid.work.dto.RouteResponse;
import org.maxsid.work.dto.UserSettingsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka-test")
@RequiredArgsConstructor
public class DebugKafkaController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/send/user-settings/{userId}")
    public ResponseEntity<String> testSendUserSettings(
            @PathVariable Long userId,
            @RequestBody UserSettingsDto userSettings) {

        kafkaProducerService.sendUserSettingsSavedEvent(userId, userSettings);
        return ResponseEntity.ok("Test message sent to user-settings-topic");
    }

//    @PostMapping("/send/route-calculated/{userId}")
//    public ResponseEntity<String> testSendRouteCalculated(
//            @PathVariable Long userId) {
//
//        RouteResponse routeResponse = RouteResponse.builder()
//                .userId(userId)
//                .homeAddress("Test Home")
//                .workAddress("Test Work")
//                .arrivalTime("09:00")
//                .travelDurationMinutes(45L)
//                .recommendedDepartureTime("08:15")
//                .message("Test route calculated")
//                .build();
//
//        kafkaProducerService.sendRouteCalculatedEvent(userId, routeResponse);
//        return ResponseEntity.ok("Test message sent to route-calculated-topic");
//    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kafka test endpoint is working");
    }
}
