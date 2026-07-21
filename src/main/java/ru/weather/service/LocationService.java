package ru.weather.service;

import ru.weather.dto.CardLocationDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.UserLocationsDto;
import ru.weather.model.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LocationService {
    CompletableFuture<List<CardLocationDto>> getAllWeathers(Long userId, List<Location> locations);

    List<Location> getLocations(Long userId);

    ResponseWithCoordinates[] findAllLocationsByName(String locationName);

    void saveNewLocation(UserLocationsDto location);

    void deleteLocation(Long userId, String latitude, String longitude);
}
