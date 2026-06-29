package org.maxsid.work.core.kafka.service;

import org.maxsid.work.dto.RouteRequest;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public interface KafkaConsumerService {

    void consumeUserSettingsFromBot(@Payload RouteRequest routeRequest,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String chatId,
                                    @Header(KafkaHeaders.CORRELATION_ID) String correlationId);

    void consumeRouteCalculationRequestFromBot(@Payload RouteRequest routeRequest,
                                               @Header(KafkaHeaders.RECEIVED_KEY) String chatId,
                                               @Header(KafkaHeaders.CORRELATION_ID) String correlationId);
}
