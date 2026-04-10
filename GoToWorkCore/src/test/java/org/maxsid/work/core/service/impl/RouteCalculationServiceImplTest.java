package org.maxsid.work.core.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.entity.UserSettings;
import org.maxsid.work.core.kafka.service.KafkaProducerService;
import org.maxsid.work.core.repository.UserSettingsRepository;
import org.maxsid.work.core.service.GeocodeService;
import org.maxsid.work.core.service.RouteService;
import org.maxsid.work.core.utils.TimeUtils;
import org.maxsid.work.dto.RouteRequest;
import org.maxsid.work.dto.RouteResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteCalculationServiceImplTest {
    @InjectMocks
    private RouteCalculationServiceImpl routeCalculationService;
    @Mock
    private GeocodeService geocodeService;
    @Mock
    private RouteService routeService;
    @Mock
    private UserSettingsRepository userSettingsRepository;
    @Mock
    private KafkaProducerService kafkaProducerService;

    private final Long TEST_USER_ID = 1L;
    private final String TEST_HOME_ADDRESS = "Санкт-Петербург, Бассейная улица 18";
    private final String TEST_WORK_ADDRESS = "Санкт-Петербург, Краснопутиловская улица 4";
    private final String TEST_TIME_ZONE = "Europe/Moscow";
    private final String TEST_ARRIVAL_TIME = "09:00";

    private UserSettings creteTestUserSettings() {
        return new UserSettings(
                TEST_USER_ID,
                TEST_HOME_ADDRESS,
                TEST_WORK_ADDRESS,
                TEST_TIME_ZONE,
                TEST_ARRIVAL_TIME
        );
    }

    private RouteRequest createTestRouteRequest() {
        RouteRequest request = new RouteRequest();
        request.setHomeAddress(TEST_HOME_ADDRESS);
        request.setWorkAddress(TEST_WORK_ADDRESS);
        request.setTimeZone(TEST_TIME_ZONE);
        request.setArrivalTime(TEST_ARRIVAL_TIME);
        return request;
    }

    private Coordinates createTestHomeCoordinates() {
        return new Coordinates(59.867416, 30.317602);
    }

    private Coordinates createTestWorkCoordinates() {
        return new Coordinates(59.875415, 30.260352);
    }

    @Test
    void calculateOptimalRoute() {
        UserSettings userSettings = creteTestUserSettings();
        Coordinates homeCoords = createTestHomeCoordinates();
        Coordinates workCoords = createTestWorkCoordinates();
        Long travelMinutes = 12L;

        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(userSettings));
        when(geocodeService.geocodeAddress(TEST_HOME_ADDRESS)).thenReturn(homeCoords);
        when(geocodeService.geocodeAddress(TEST_WORK_ADDRESS)).thenReturn(workCoords);
        when(routeService.calculateTravelTimeToWork(homeCoords, workCoords)).thenReturn(travelMinutes);

        try (MockedStatic<TimeUtils> timeUtilsMockedStatic = mockStatic(TimeUtils.class)) {
            String expectedDepartureTime = "08:18";
            timeUtilsMockedStatic.when(() -> TimeUtils.calculateDepartureTime(userSettings.getArrivalTimeToWork(), travelMinutes))
                    .thenReturn(expectedDepartureTime);

            RouteResponse routeResponse = routeCalculationService.calculateOptimalRoute(TEST_USER_ID);

            assertThat(routeResponse).isNotNull();
            assertThat(routeResponse.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(routeResponse.getHomeAddress()).isEqualTo(TEST_HOME_ADDRESS);
            assertThat(routeResponse.getWorkAddress()).isEqualTo(TEST_WORK_ADDRESS);
            assertThat(routeResponse.getTravelDurationMinutes()).isEqualTo(travelMinutes);
            assertThat(routeResponse.getRecommendedDepartureTime()).isEqualTo(expectedDepartureTime);

            verify(kafkaProducerService).sendRouteCalculatedEvent(eq(TEST_USER_ID), any(RouteResponse.class));
        }
    }

    @Test
    void saveUserSettings() {
        RouteRequest request = createTestRouteRequest();
        UserSettings savedSettings = creteTestUserSettings();

        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(savedSettings);

        UserSettings result = routeCalculationService.saveUserSettings(TEST_USER_ID, request);

        // Ловим объект, который передается в save
        ArgumentCaptor<UserSettings> captor = ArgumentCaptor.forClass(UserSettings.class);
        verify(userSettingsRepository).save(captor.capture());

        assertThat(result).isNotNull();

        UserSettings captured = captor.getValue();
        assertThat(captured.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(captured.getHomeAddress()).isEqualTo(request.getHomeAddress());
        assertThat(captured.getWorkAddress()).isEqualTo(request.getWorkAddress());
        assertThat(captured.getArrivalTimeToWork()).isEqualTo(request.getArrivalTime());

        verify(kafkaProducerService).sendUserSettingsSavedEvent(eq(TEST_USER_ID), any());
    }

    @Test
    void getUserSettings() {
        UserSettings expectedSettings = creteTestUserSettings();
        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(expectedSettings));

        Optional<UserSettings> result = routeCalculationService.getUserSettings(TEST_USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(TEST_USER_ID);
        verify(userSettingsRepository).findByUserId(TEST_USER_ID);
    }
}