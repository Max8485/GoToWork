package org.maxsid.work.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.maxsid.work.core.dto.RouteRequest;
import org.maxsid.work.core.dto.RouteResponse;
import org.maxsid.work.bot.service.CoreServiceClient;

import org.maxsid.work.core.entity.UserSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@RequiredArgsConstructor
@Service
public class CoreServiceClientImpl implements CoreServiceClient {

    @Value("${bot.url:http://localhost:8080/api/routes}")
    private String coreServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public UserSettings saveUserSettings(Long userId, RouteRequest request) {
        String url = coreServiceUrl + "/users/" + userId + "/settings";
        ResponseEntity<UserSettings> response = restTemplate.postForEntity(url, request, UserSettings.class);
        return response.getBody();
    }

    @Override
    public RouteResponse calculateRoute(Long userId) {
        String url = coreServiceUrl + "/users/" + userId + "/calculate";
        ResponseEntity<RouteResponse> response = restTemplate.getForEntity(url, RouteResponse.class);
        return response.getBody();
    }

    @Override
    public UserSettings getUserSettings(Long userId) {
        String url = coreServiceUrl + "/users/" + userId + "/settings";
        ResponseEntity<UserSettings> response = restTemplate.getForEntity(url, UserSettings.class);
        return response.getBody();
    }
}
