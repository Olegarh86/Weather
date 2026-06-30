package ru.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainDto {
    private String temp;
    @JsonProperty("feels_like")
    private String feelsLike;
    @JsonProperty("temp_min")
    private String tempMin;
    @JsonProperty("temp_max")
    private String tempMax;
    private String pressure;
    private String humidity;
    @JsonProperty("sea_level")
    private String seaLevel;
    @JsonProperty("grnd_level")
    private String grndLevel;
}
