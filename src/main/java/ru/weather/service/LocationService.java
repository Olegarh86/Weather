package ru.weather.service;

import ru.weather.dto.CardLocationDto;
import ru.weather.dto.CurrentUserDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.UserLocationsDto;
import ru.weather.model.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LocationService {
    CompletableFuture<List<CardLocationDto>> getAllWeathers(CurrentUserDto user, List<Location> locations);

    List<Location> getLocations(CurrentUserDto user);

    ResponseWithCoordinates[] findAllLocationsByName(String locationName);

    void saveNewLocation(UserLocationsDto location, CurrentUserDto user);

    void deleteLocation(CurrentUserDto user, String latitude, String longitude);
}
