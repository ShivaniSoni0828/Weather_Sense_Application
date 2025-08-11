package com.zai.weather.dto.openweather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for OpenWeatherMap API response.
 * Only maps the fields we need for our unified response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherResponse {
    
    @JsonProperty("main")
    private Main main;
    
    @JsonProperty("wind")
    private Wind wind;
    
    @JsonProperty("cod")
    private Integer code;
    
    @JsonProperty("message")
    private String message;
    
    public Main getMain() {
        return main;
    }
    
    public void setMain(Main main) {
        this.main = main;
    }
    
    public Wind getWind() {
        return wind;
    }
    
    public void setWind(Wind wind) {
        this.wind = wind;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private Double temp;
        
        public Double getTemp() {
            return temp;
        }
        
        public void setTemp(Double temp) {
            this.temp = temp;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;
        
        public Double getSpeed() {
            return speed;
        }
        
        public void setSpeed(Double speed) {
            this.speed = speed;
        }
    }
}
