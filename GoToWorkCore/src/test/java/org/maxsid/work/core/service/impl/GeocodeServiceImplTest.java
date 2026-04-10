package org.maxsid.work.core.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxsid.work.core.coordinates.Coordinates;
import org.maxsid.work.core.model.AddressData;
import org.maxsid.work.core.model.DaDataResponse;
import org.maxsid.work.core.model.Suggestion;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeocodeServiceImplTest {

    @InjectMocks
    private GeocodeServiceImpl geocodeService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        java.lang.reflect.Field apiKeyField = GeocodeServiceImpl.class.getDeclaredField("dadataApiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(geocodeService, "test-api-key");

        java.lang.reflect.Field urlField = GeocodeServiceImpl.class.getDeclaredField("dadataUrl");
        urlField.setAccessible(true);
        urlField.set(geocodeService, "https://test.dadata.ru");

        java.lang.reflect.Field decimalFormatField = GeocodeServiceImpl.class.getDeclaredField("decimalFormat");
        decimalFormatField.setAccessible(true);

        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(java.util.Locale.US);
        symbols.setDecimalSeparator('.');
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.######", symbols);
        decimalFormat.setParseBigDecimal(true);

        decimalFormatField.set(geocodeService, decimalFormat);
    }

    @Test
    void geocodeAddress() {
        DaDataResponse dataResponse = new DaDataResponse();
        Suggestion suggestion = new Suggestion();
        AddressData data = new AddressData();
        data.setGeo_lat("59.9386");
        data.setGeo_lon("30.3141");
        suggestion.setData(data);
        dataResponse.setSuggestions(Collections.singletonList(suggestion));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(DaDataResponse.class)
        )).thenReturn(new ResponseEntity<>(dataResponse, HttpStatus.OK));

        String TEST_ADDRESS = "Санкт-Петербург, Бассейная улица 18";
        Coordinates result = geocodeService.geocodeAddress(TEST_ADDRESS);

        assertThat(result).isNotNull();
        assertThat(result.getLat()).isEqualTo(59.9386);
        assertThat(result.getLon()).isEqualTo(30.3141);
    }
}