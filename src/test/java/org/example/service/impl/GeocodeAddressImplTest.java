package org.example.service.impl;

import org.example.coordinates.Coordinates;
import org.example.feign.DadataFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@ActiveProfiles("test")
class GeocodeAddressImplTest {

//    @Mock
//    private DadataFeignClient dadataFeignClient;
//
//    @InjectMocks
//    private GeocodeServiceImpl geocodingService;
//
//    @Test
//    public void testGeocodeAddress_Success() {
//        // Mock response from Dadata
//        Map<String, Object> mockResponse = Map.of(
//                "suggestions", java.util.List.of(
//                        Map.of(
//                                "data", Map.of(
//                                        "geo_lat", "55.12345",
//                                        "geo_lon", "37.12345"
//                                )
//                        )
//                )
//        );
//
//        when(dadataFeignClient.geocodeAddress(anyString(), anyString(), anyMap()))
//                .thenReturn(mockResponse);
//
//        Coordinates result = geocodingService.geocodeAddress("Москва, Кутузовский проспект 32");
//
//        assertNotNull(result);
//        assertEquals(55.12345, result.getLat());
//        assertEquals(37.12345, result.getLon());
//    }
}