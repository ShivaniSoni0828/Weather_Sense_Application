package com.zai.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application class for the Weather Service.
 * Enables caching for weather data to improve performance and reduce API calls.
 */
@SpringBootApplication
@EnableCaching
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
