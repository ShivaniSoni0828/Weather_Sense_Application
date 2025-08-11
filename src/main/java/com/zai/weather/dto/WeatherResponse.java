package com.zai.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Unified weather response DTO that represents the standardized output
 * regardless of which weather provider is used.
 */
public class WeatherResponse {
    
    @JsonProperty("temperature_degrees")
    private Double temperatureDegrees;
    
    @JsonProperty("wind_speed")
    private Double windSpeed;
    
    public WeatherResponse() {}
    
    public WeatherResponse(Double temperatureDegrees, Double windSpeed) {
        this.temperatureDegrees = temperatureDegrees;
        this.windSpeed = windSpeed;
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
    
    @Override
    public String toString() {
        return "WeatherResponse{" +
                "temperatureDegrees=" + temperatureDegrees +
                ", windSpeed=" + windSpeed +
                '}';
    }
}
