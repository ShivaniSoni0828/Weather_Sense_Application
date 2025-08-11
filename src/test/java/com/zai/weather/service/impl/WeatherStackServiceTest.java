package com.zai.weather.service.impl;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.dto.weatherstack.WeatherStackResponse;
import com.zai.weather.exception.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WeatherStackService.
 */
@ExtendWith(MockitoExtension.class)
class WeatherStackServiceTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    
    private WeatherStackService weatherStackService;
    
    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.setConnectTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        
        weatherStackService = new WeatherStackService(
                restTemplateBuilder, 
                "test-api-key", 
                "http://api.weatherstack.com/current", 
                5000
        );
    }
    
    @Test
    void getWeatherData_Success() throws Exception {
        // Given
        WeatherStackResponse mockResponse = new WeatherStackResponse();
        WeatherStackResponse.Current current = new WeatherStackResponse.Current();
        current.setTemperature(25.0);
        current.setWindSpeed(15.0);
        mockResponse.setCurrent(current);
        
        when(restTemplate.getForObject(anyString(), eq(WeatherStackResponse.class)))
                .thenReturn(mockResponse);
        
        // When
        WeatherResponse result = weatherStackService.getWeatherData("melbourne");
        
        // Then
        assertNotNull(result);
        assertEquals(25.0, result.getTemperatureDegrees());
        assertEquals(15.0, result.getWindSpeed());
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherStackResponse.class));
    }
    
    @Test
    void getWeatherData_ApiError() {
        // Given
        WeatherStackResponse mockResponse = new WeatherStackResponse();
        WeatherStackResponse.Error error = new WeatherStackResponse.Error();
        error.setCode(101);
        error.setInfo("Invalid API key");
        mockResponse.setError(error);
        
        when(restTemplate.getForObject(anyString(), eq(WeatherStackResponse.class)))
                .thenReturn(mockResponse);
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> weatherStackService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherStackResponse.class));
    }
    
    @Test
    void getWeatherData_NullResponse() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(WeatherStackResponse.class)))
                .thenReturn(null);
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> weatherStackService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherStackResponse.class));
    }
    
    @Test
    void getWeatherData_RestClientException() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(WeatherStackResponse.class)))
                .thenThrow(new RestClientException("Connection timeout"));
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> weatherStackService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherStackResponse.class));
    }
    
    @Test
    void getProviderName() {
        // When & Then
        assertEquals("WeatherStack", weatherStackService.getProviderName());
    }
}
