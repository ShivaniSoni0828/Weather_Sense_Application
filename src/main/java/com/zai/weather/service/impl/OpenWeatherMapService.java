package com.zai.weather.service.impl;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.dto.openweather.OpenWeatherResponse;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.service.WeatherProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * OpenWeatherMap API implementation of the weather provider service.
 * Serves as the failover weather data source.
 */
@Service
public class OpenWeatherMapService implements WeatherProviderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherMapService.class);
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    
    public OpenWeatherMapService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${weather.providers.openweathermap.api-key}") String apiKey,
            @Value("${weather.providers.openweathermap.url}") String baseUrl,
            @Value("${weather.providers.openweathermap.timeout:5000}") int timeout) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    @Override
    public WeatherResponse getWeatherData(String city) throws Exception {
        logger.debug("Fetching weather data from OpenWeatherMap for city: {}", city);
        
        // OpenWeatherMap requires country code for Melbourne
        String query = city.toLowerCase().contains("melbourne") ? "melbourne,AU" : city;
        String url = String.format("%s?q=%s&appid=%s&units=metric", baseUrl, query, apiKey);
        
        try {
            OpenWeatherResponse response = restTemplate.getForObject(url, OpenWeatherResponse.class);
            
            if (response == null) {
                throw new WeatherServiceException("OpenWeatherMap returned null response");
            }
            
            if (response.getCode() != null && response.getCode() != 200) {
                throw new WeatherServiceException("OpenWeatherMap API error: " + response.getMessage());
            }
            
            if (response.getMain() == null || response.getWind() == null) {
                throw new WeatherServiceException("OpenWeatherMap response missing weather data");
            }
            
            // Convert wind speed from m/s to km/h to match WeatherStack format
            double windSpeedKmh = response.getWind().getSpeed() * 3.6;
            
            WeatherResponse weatherResponse = new WeatherResponse(
                    response.getMain().getTemp(),
                    windSpeedKmh
            );
            
            logger.debug("Successfully fetched weather data from OpenWeatherMap: {}", weatherResponse);
            return weatherResponse;
            
        } catch (Exception e) {
            logger.error("Error fetching weather data from OpenWeatherMap: {}", e.getMessage());
            throw new WeatherServiceException("OpenWeatherMap service unavailable: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "OpenWeatherMap";
    }
}
