package com.zai.weather.controller;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for weather-related endpoints.
 * Provides the main API endpoint for retrieving weather data.
 */
@RestController
public class WeatherController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    private final WeatherService weatherService;
    
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    /**
     * Gets weather data for the specified city.
     * 
     * @param city The city name (defaults to melbourne if not provided)
     * @return WeatherResponse containing temperature in Celsius and wind speed in km/h
     */
    @GetMapping("/v1/weather")
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam(name = "city", defaultValue = "melbourne") String city) {
        
        logger.info("Weather data requested for city: {}", city);
        
        try {
            WeatherResponse response = weatherService.getWeatherData(city);
            logger.debug("Returning weather data for city {}: {}", city, response);
            return ResponseEntity.ok(response);
            
        } catch (WeatherServiceException e) {
            logger.error("Failed to get weather data for city {}: {}", city, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
    
    /**
     * Health check endpoint to verify the service is running.
     */
    @GetMapping("/v1/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather service is running");
    }
    
    /**
     * Root endpoint providing API information.
     */
    @GetMapping("/")
    public ResponseEntity<java.util.Map<String, Object>> root() {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        info.put("service", "Melbourne Weather Service");
        info.put("version", "1.0.0");
        info.put("endpoints", java.util.Map.of(
            "weather", "/v1/weather?city=melbourne",
            "health", "/v1/health"
        ));
        info.put("description", "Provides Melbourne weather data with failover between WeatherStack and OpenWeatherMap APIs");
        return ResponseEntity.ok(info);
    }
}
