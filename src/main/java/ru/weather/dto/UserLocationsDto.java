package ru.weather.dto;

import lombok.Getter;
import ru.weather.model.WeatherLocation;

import java.util.List;

@Getter
public class UserLocationsDto {
    private long login;
    private List<WeatherLocation> locations;

    public UserLocationsDto(long login, List<WeatherLocation> locations) {
        this.login = login;
        this.locations = locations;
    }
}
