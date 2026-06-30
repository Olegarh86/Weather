package ru.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDto {
    private Long id;
    private String main;
    private String description;
    private String icon;
}
