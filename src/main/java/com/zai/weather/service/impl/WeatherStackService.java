package com.zai.weather.service.impl;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.dto.weatherstack.WeatherStackResponse;
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
 * WeatherStack API implementation of the weather provider service.
 * Serves as the primary weather data source.
 */
@Service
public class WeatherStackService implements WeatherProviderService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherStackService.class);
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    
    public WeatherStackService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${weather.providers.weatherstack.api-key}") String apiKey,
            @Value("${weather.providers.weatherstack.url}") String baseUrl,
            @Value("${weather.providers.weatherstack.timeout:5000}") int timeout) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    @Override
    public WeatherResponse getWeatherData(String city) throws Exception {
        logger.debug("Fetching weather data from WeatherStack for city: {}", city);
        
        String url = String.format("%s?access_key=%s&query=%s", baseUrl, apiKey, city);
        
        try {
            WeatherStackResponse response = restTemplate.getForObject(url, WeatherStackResponse.class);
            
            if (response == null) {
                throw new WeatherServiceException("WeatherStack returned null response");
            }
            
            if (response.getError() != null) {
                throw new WeatherServiceException("WeatherStack API error: " + response.getError().getInfo());
            }
            
            if (response.getCurrent() == null) {
                throw new WeatherServiceException("WeatherStack response missing current weather data");
            }
            
            WeatherStackResponse.Current current = response.getCurrent();
            WeatherResponse weatherResponse = new WeatherResponse(
                    current.getTemperature(),
                    current.getWindSpeed()
            );
            
            logger.debug("Successfully fetched weather data from WeatherStack: {}", weatherResponse);
            return weatherResponse;
            
        } catch (Exception e) {
            logger.error("Error fetching weather data from WeatherStack: {}", e.getMessage());
            throw new WeatherServiceException("WeatherStack service unavailable: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "WeatherStack";
    }
}
