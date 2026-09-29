package com.viajajunto.api.integrations.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GooglePlacesClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GooglePlacesClient googlePlacesClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googlePlacesClient, "baseUrl", "https://maps.googleapis.com/maps/api/place");
    }

    @Test
    @DisplayName("Deve retornar resposta mock quando apiKey for 'mock-key'")
    void shouldReturnMockWhenApiKeyIsMock() {
        ReflectionTestUtils.setField(googlePlacesClient, "apiKey", "mock-key");

        PlacesResponseDTO response = googlePlacesClient.searchPlaces("Roma");

        assertNotNull(response);
        assertEquals("OK", response.getStatus());
        assertTrue(response.getResults().isEmpty());
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Deve retornar resposta mock quando apiKey estiver em branco")
    void shouldReturnMockWhenApiKeyIsBlank() {
        ReflectionTestUtils.setField(googlePlacesClient, "apiKey", "   ");

        PlacesResponseDTO response = googlePlacesClient.searchPlaces("Roma");

        assertNotNull(response);
        assertEquals("OK", response.getStatus());
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Deve chamar RestTemplate quando apiKey for válida")
    void shouldCallRestTemplateWhenApiKeyIsValid() {
        ReflectionTestUtils.setField(googlePlacesClient, "apiKey", "real-api-key");
        PlacesResponseDTO expected = PlacesResponseDTO.builder()
                .status("OK")
                .results(List.of())
                .build();

        when(restTemplate.getForObject(anyString(), eq(PlacesResponseDTO.class))).thenReturn(expected);

        PlacesResponseDTO response = googlePlacesClient.searchPlaces("Roma");

        assertNotNull(response);
        assertEquals("OK", response.getStatus());
        verify(restTemplate, times(1)).getForObject(contains("real-api-key"), eq(PlacesResponseDTO.class));
    }

    @Test
    @DisplayName("Deve tratar erro de comunicação HTTP e retornar status ERROR")
    void shouldHandleExceptionFromRestTemplate() {
        ReflectionTestUtils.setField(googlePlacesClient, "apiKey", "real-api-key");
        when(restTemplate.getForObject(anyString(), eq(PlacesResponseDTO.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        PlacesResponseDTO response = googlePlacesClient.searchPlaces("Roma");

        assertNotNull(response);
        assertEquals("ERROR", response.getStatus());
        assertTrue(response.getResults().isEmpty());
    }
}
