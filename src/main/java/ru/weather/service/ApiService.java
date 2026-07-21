package ru.weather.service;

import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;

public interface ApiService {
    ResponseWithWeatherDto getWeather(String latitude, String longitude);

    ResponseWithCoordinates[] findAllLocations(String name);
}
