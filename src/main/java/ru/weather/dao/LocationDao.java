package ru.weather.dao;

import ru.weather.model.Location;

import java.util.List;

public interface LocationDao {
    List<Location> getLocationsByUserId(long userId);

    void saveLocation(Location location);

    void deleteLocation(Long userId, Double latitude, Double longitude);
}
