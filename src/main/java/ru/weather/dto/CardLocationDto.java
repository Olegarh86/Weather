package ru.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardLocationDto {
    private String icon;
    private String temp;
    private String name;
    private String country;
    @JsonProperty("feels_like")
    private String feelsLike;
    private String description;
    private String humidity;
    private String latitude;
    private String longitude;
}
