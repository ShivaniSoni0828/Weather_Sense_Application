package com.zai.weather.service;

import com.zai.weather.dto.WeatherResponse;

/**
 * Interface for weather provider services.
 * Allows for different implementations (WeatherStack, OpenWeatherMap, etc.)
 */
public interface WeatherProviderService {
    
    /**
     * Fetches weather data for a specific city from the weather provider.
     * 
     * @param city The city name
     * @return WeatherResponse containing temperature and wind speed
     * @throws Exception if the weather provider is unavailable or returns an error
     */
    WeatherResponse getWeatherData(String city) throws Exception;
    
    /**
     * Returns the name of the weather provider.
     * 
     * @return Provider name
     */
    String getProviderName();
}
