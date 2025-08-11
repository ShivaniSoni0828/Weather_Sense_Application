package com.zai.weather.service;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.entity.WeatherData;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WeatherService.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {
    
    @Mock
    private WeatherProviderService primaryProvider;
    
    @Mock
    private WeatherProviderService fallbackProvider;
    
    @Mock
    private WeatherRepository weatherRepository;
    
    private WeatherService weatherService;
    
    private WeatherResponse mockWeatherResponse;
    
    @BeforeEach
    void setUp() {
        // Create WeatherService with mocked providers
        weatherService = new WeatherService(Arrays.asList(primaryProvider, fallbackProvider), weatherRepository);
        mockWeatherResponse = new WeatherResponse(25.0, 15.0);
    }
    
    @Test
    void getWeatherData_PrimaryProviderSuccess() throws Exception {
        // Given
        when(primaryProvider.getProviderName()).thenReturn("Primary");
        when(primaryProvider.getWeatherData("melbourne")).thenReturn(mockWeatherResponse);
        when(weatherRepository.findByCityIgnoreCase("melbourne")).thenReturn(Optional.empty());
        
        // When
        WeatherResponse result = weatherService.getWeatherData("melbourne");
        
        // Then
        assertEquals(mockWeatherResponse.getTemperatureDegrees(), result.getTemperatureDegrees());
        assertEquals(mockWeatherResponse.getWindSpeed(), result.getWindSpeed());
        
        verify(primaryProvider, times(1)).getWeatherData("melbourne");
        verify(fallbackProvider, never()).getWeatherData(anyString());
        verify(weatherRepository, times(1)).save(any(WeatherData.class));
    }
    
    @Test
    void getWeatherData_FallbackProviderSuccess() throws Exception {
        // Given
        when(primaryProvider.getProviderName()).thenReturn("Primary");
        when(fallbackProvider.getProviderName()).thenReturn("Fallback");
        when(primaryProvider.getWeatherData("melbourne")).thenThrow(new RuntimeException("Primary failed"));
        when(fallbackProvider.getWeatherData("melbourne")).thenReturn(mockWeatherResponse);
        when(weatherRepository.findByCityIgnoreCase("melbourne")).thenReturn(Optional.empty());
        
        // When
        WeatherResponse result = weatherService.getWeatherData("melbourne");
        
        // Then
        assertEquals(mockWeatherResponse.getTemperatureDegrees(), result.getTemperatureDegrees());
        assertEquals(mockWeatherResponse.getWindSpeed(), result.getWindSpeed());
        
        verify(primaryProvider, times(1)).getWeatherData("melbourne");
        verify(fallbackProvider, times(1)).getWeatherData("melbourne");
        verify(weatherRepository, times(1)).save(any(WeatherData.class));
    }
    
    @Test
    void getWeatherData_AllProvidersFailButStaleDataAvailable() throws Exception {
        // Given
        WeatherData staleData = new WeatherData("melbourne", 20.0, 10.0, "Primary");
        
        when(primaryProvider.getWeatherData("melbourne")).thenThrow(new RuntimeException("Primary failed"));
        when(fallbackProvider.getWeatherData("melbourne")).thenThrow(new RuntimeException("Fallback failed"));
        when(weatherRepository.findLatestByCityIgnoreCase("melbourne")).thenReturn(Optional.of(staleData));
        
        // When
        WeatherResponse result = weatherService.getWeatherData("melbourne");
        
        // Then
        assertEquals(20.0, result.getTemperatureDegrees());
        assertEquals(10.0, result.getWindSpeed());
        
        verify(primaryProvider, times(1)).getWeatherData("melbourne");
        verify(fallbackProvider, times(1)).getWeatherData("melbourne");
        verify(weatherRepository, times(1)).findLatestByCityIgnoreCase("melbourne");
    }
    
    @Test
    void getWeatherData_AllProvidersFailAndNoStaleData() throws Exception {
        // Given
        when(primaryProvider.getWeatherData("melbourne")).thenThrow(new RuntimeException("Primary failed"));
        when(fallbackProvider.getWeatherData("melbourne")).thenThrow(new RuntimeException("Fallback failed"));
        when(weatherRepository.findLatestByCityIgnoreCase("melbourne")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(WeatherServiceException.class, () -> weatherService.getWeatherData("melbourne"));
        
        verify(primaryProvider, times(1)).getWeatherData("melbourne");
        verify(fallbackProvider, times(1)).getWeatherData("melbourne");
        verify(weatherRepository, times(1)).findLatestByCityIgnoreCase("melbourne");
    }
}
