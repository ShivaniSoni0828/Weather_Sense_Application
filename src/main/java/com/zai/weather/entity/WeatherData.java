package com.zai.weather.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing cached weather data in the database.
 * Used for persistence and serving stale data when APIs are unavailable.
 */
@Entity
@Table(name = "weather_data")
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String city;
    
    @Column(name = "temperature_degrees")
    private Double temperatureDegrees;
    
    @Column(name = "wind_speed")
    private Double windSpeed;
    
    @Column(name = "provider_source")
    private String providerSource;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public WeatherData() {}
    
    public WeatherData(String city, Double temperatureDegrees, Double windSpeed, String providerSource) {
        this.city = city;
        this.temperatureDegrees = temperatureDegrees;
        this.windSpeed = windSpeed;
        this.providerSource = providerSource;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public Double getTemperatureDegrees() {
        return temperatureDegrees;
    }
    
    public void setTemperatureDegrees(Double temperatureDegrees) {
        this.temperatureDegrees = temperatureDegrees;
    }
    
    public Double getWindSpeed() {
        return windSpeed;
    }
    
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }
    
    public String getProviderSource() {
        return providerSource;
    }
    
    public void setProviderSource(String providerSource) {
        this.providerSource = providerSource;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
