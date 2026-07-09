package ru.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("user_id")
    private Long userId;
    private Double latitude;
    private Double longitude;
}
