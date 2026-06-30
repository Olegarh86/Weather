package ru.weather.dto;

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
    private String feels_like;
    private String description;
    private String humidity;
    private String latitude;
    private String longitude;
}
