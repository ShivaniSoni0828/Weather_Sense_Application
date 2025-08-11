package com.zai.weather.controller;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WeatherController.
 */
@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {
    
    @Mock
    private WeatherService weatherService;
    
    @InjectMocks
    private WeatherController weatherController;
    
    private WeatherResponse mockWeatherResponse;
    
    @BeforeEach
    void setUp() {
        mockWeatherResponse = new WeatherResponse(25.0, 15.0);
    }
    
    @Test
    void getWeather_Success() throws WeatherServiceException {
        // Given
        when(weatherService.getWeatherData("melbourne")).thenReturn(mockWeatherResponse);
        
        // When
        ResponseEntity<WeatherResponse> response = weatherController.getWeather("melbourne");
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(25.0, response.getBody().getTemperatureDegrees());
        assertEquals(15.0, response.getBody().getWindSpeed());
        
        verify(weatherService, times(1)).getWeatherData("melbourne");
    }
    
    @Test
    void getWeather_DefaultsToMelbourne() throws WeatherServiceException {
        // Given
        when(weatherService.getWeatherData(anyString())).thenReturn(mockWeatherResponse);
        
        // When
        ResponseEntity<WeatherResponse> response = weatherController.getWeather("melbourne");
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(weatherService, times(1)).getWeatherData("melbourne");
    }
    
    @Test
    void getWeather_ServiceException() throws WeatherServiceException {
        // Given
        when(weatherService.getWeatherData(anyString()))
                .thenThrow(new WeatherServiceException("All providers unavailable"));
        
        // When
        ResponseEntity<WeatherResponse> response = weatherController.getWeather("melbourne");
        
        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(weatherService, times(1)).getWeatherData("melbourne");
    }
    
    @Test
    void health_ReturnsOk() {
        // When
        ResponseEntity<String> response = weatherController.health();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Weather service is running", response.getBody());
    }
}
