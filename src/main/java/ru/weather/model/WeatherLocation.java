package ru.weather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherLocation {
    private String name;
    private Long user_id;
    private Double latitude;
    private Double longitude;
}
