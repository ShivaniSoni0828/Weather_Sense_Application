package com.zai.weather.repository;

import com.zai.weather.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for WeatherData entity operations.
 * Provides methods to interact with the H2 database for weather data persistence.
 */
@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    
    /**
     * Finds the most recent weather data for a specific city.
     * Used for serving stale data when all weather providers are unavailable.
     */
    @Query("SELECT w FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) ORDER BY w.updatedAt DESC")
    Optional<WeatherData> findLatestByCityIgnoreCase(@Param("city") String city);
    
    /**
     * Finds weather data by city name (case-insensitive).
     */
    Optional<WeatherData> findByCityIgnoreCase(String city);
}
