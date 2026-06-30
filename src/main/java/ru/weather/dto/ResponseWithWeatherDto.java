package ru.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWithWeatherDto {
    private Long id;
    private String name;
    private CoordDto coord;
    private CountryDto sys;
    private List<WeatherDto> weather;
    private MainDto main;
}
