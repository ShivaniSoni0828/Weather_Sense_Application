package com.zai.weather.service.impl;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.dto.openweather.OpenWeatherResponse;
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
 * Unit tests for OpenWeatherMapService.
 */
@ExtendWith(MockitoExtension.class)
class OpenWeatherMapServiceTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    
    private OpenWeatherMapService openWeatherMapService;
    
    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.setConnectTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        
        openWeatherMapService = new OpenWeatherMapService(
                restTemplateBuilder, 
                "test-api-key", 
                "http://api.openweathermap.org/data/2.5/weather", 
                5000
        );
    }
    
    @Test
    void getWeatherData_Success() throws Exception {
        // Given
        OpenWeatherResponse mockResponse = new OpenWeatherResponse();
        mockResponse.setCode(200);
        
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(25.0);
        mockResponse.setMain(main);
        
        OpenWeatherResponse.Wind wind = new OpenWeatherResponse.Wind();
        wind.setSpeed(4.17); // m/s which should be converted to ~15 km/h
        mockResponse.setWind(wind);
        
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(mockResponse);
        
        // When
        WeatherResponse result = openWeatherMapService.getWeatherData("melbourne");
        
        // Then
        assertNotNull(result);
        assertEquals(25.0, result.getTemperatureDegrees());
        assertEquals(15.012, result.getWindSpeed(), 0.1); // 4.17 * 3.6
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void getWeatherData_ApiError() {
        // Given
        OpenWeatherResponse mockResponse = new OpenWeatherResponse();
        mockResponse.setCode(401);
        mockResponse.setMessage("Invalid API key");
        
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(mockResponse);
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> openWeatherMapService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void getWeatherData_NullResponse() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenReturn(null);
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> openWeatherMapService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void getWeatherData_RestClientException() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class)))
                .thenThrow(new RestClientException("Connection timeout"));
        
        // When & Then
        assertThrows(WeatherServiceException.class, 
                () -> openWeatherMapService.getWeatherData("melbourne"));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void getProviderName() {
        // When & Then
        assertEquals("OpenWeatherMap", openWeatherMapService.getProviderName());
    }
}
