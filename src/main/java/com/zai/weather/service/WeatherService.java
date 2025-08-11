package com.zai.weather.service;

import com.zai.weather.dto.WeatherResponse;
import com.zai.weather.entity.WeatherData;
import com.zai.weather.exception.WeatherServiceException;
import com.zai.weather.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Main weather service that orchestrates weather data retrieval with failover logic.
 * Implements caching and stale data serving capabilities.
 */
@Service
@Transactional
public class WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    private final List<WeatherProviderService> weatherProviders;
    private final WeatherRepository weatherRepository;
    
    public WeatherService(List<WeatherProviderService> weatherProviders, WeatherRepository weatherRepository) {
        this.weatherProviders = weatherProviders;
        this.weatherRepository = weatherRepository;
        
        logger.info("Initialized WeatherService with {} providers: {}", 
                weatherProviders.size(), 
                weatherProviders.stream().map(WeatherProviderService::getProviderName).toList());
    }
    
    /**
     * Gets weather data for a city with caching and failover logic.
     * Cache TTL is configured to 3 seconds in application.yml.
     * 
     * @param city The city name
     * @return WeatherResponse containing temperature and wind speed
     * @throws WeatherServiceException if all providers fail and no stale data is available
     */
    @Cacheable(value = "weatherData", key = "#city.toLowerCase()")
    public WeatherResponse getWeatherData(String city) throws WeatherServiceException {
        logger.debug("Fetching weather data for city: {}", city);
        
        // Try each weather provider in order (primary first, then failover)
        for (WeatherProviderService provider : weatherProviders) {
            try {
                logger.debug("Attempting to fetch data from provider: {}", provider.getProviderName());
                WeatherResponse response = provider.getWeatherData(city);
                
                // Save successful response to database for stale data serving
                saveWeatherData(city, response, provider.getProviderName());
                
                logger.info("Successfully fetched weather data for {} from {}", city, provider.getProviderName());
                return response;
                
            } catch (Exception e) {
                logger.warn("Provider {} failed for city {}: {}", provider.getProviderName(), city, e.getMessage());
                // Continue to next provider
            }
        }
        
        // All providers failed, try to serve stale data
        logger.warn("All weather providers failed for city: {}. Attempting to serve stale data.", city);
        return getStaleWeatherData(city);
    }
    
    /**
     * Saves weather data to the database for stale data serving.
     */
    private void saveWeatherData(String city, WeatherResponse response, String providerName) {
        try {
            Optional<WeatherData> existingData = weatherRepository.findByCityIgnoreCase(city);
            
            WeatherData weatherData;
            if (existingData.isPresent()) {
                weatherData = existingData.get();
                weatherData.setTemperatureDegrees(response.getTemperatureDegrees());
                weatherData.setWindSpeed(response.getWindSpeed());
                weatherData.setProviderSource(providerName);
            } else {
                weatherData = new WeatherData(city, response.getTemperatureDegrees(), 
                        response.getWindSpeed(), providerName);
            }
            
            weatherRepository.save(weatherData);
            logger.debug("Saved weather data to database for city: {}", city);
            
        } catch (Exception e) {
            logger.error("Failed to save weather data to database for city {}: {}", city, e.getMessage());
            // Don't throw exception here as the main functionality should continue
        }
    }
    
    /**
     * Retrieves stale weather data from the database when all providers are unavailable.
     */
    private WeatherResponse getStaleWeatherData(String city) throws WeatherServiceException {
        Optional<WeatherData> staleData = weatherRepository.findLatestByCityIgnoreCase(city);
        
        if (staleData.isPresent()) {
            WeatherData data = staleData.get();
            WeatherResponse response = new WeatherResponse(data.getTemperatureDegrees(), data.getWindSpeed());
            
            logger.info("Serving stale weather data for city: {} (last updated from: {})", 
                    city, data.getProviderSource());
            return response;
        }
        
        throw new WeatherServiceException("All weather providers are unavailable and no stale data exists for city: " + city);
    }
}
