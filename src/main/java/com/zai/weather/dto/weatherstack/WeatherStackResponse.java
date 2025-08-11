package com.zai.weather.dto.weatherstack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for WeatherStack API response.
 * Only maps the fields we need for our unified response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherStackResponse {
    
    @JsonProperty("current")
    private Current current;
    
    @JsonProperty("error")
    private Error error;
    
    public Current getCurrent() {
        return current;
    }
    
    public void setCurrent(Current current) {
        this.current = current;
    }
    
    public Error getError() {
        return error;
    }
    
    public void setError(Error error) {
        this.error = error;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        @JsonProperty("temperature")
        private Double temperature;
        
        @JsonProperty("wind_speed")
        private Double windSpeed;
        
        public Double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Double getWindSpeed() {
            return windSpeed;
        }
        
        public void setWindSpeed(Double windSpeed) {
            this.windSpeed = windSpeed;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        @JsonProperty("code")
        private Integer code;
        
        @JsonProperty("info")
        private String info;
        
        public Integer getCode() {
            return code;
        }
        
        public void setCode(Integer code) {
            this.code = code;
        }
        
        public String getInfo() {
            return info;
        }
        
        public void setInfo(String info) {
            this.info = info;
        }
    }
}
