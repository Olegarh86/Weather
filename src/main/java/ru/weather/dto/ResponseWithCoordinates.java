package ru.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWithCoordinates {
    private String name;
    private String lat;
    private String lon;
    private String country;
    private String state;
}
