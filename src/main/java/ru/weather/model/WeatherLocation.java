package ru.weather.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherLocation {
    private String name;
    private String latitude;
    private String longitude;
    private String country;
    private String state;
}
