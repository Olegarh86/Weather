package ru.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDto {
    private int id;
    private String main;
    private String description;
    private String icon;
}
