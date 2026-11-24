package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.weather.dao.LocationDao;
import ru.weather.dto.*;
import ru.weather.exception.*;
import ru.weather.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationDao locationDaoImpl;
    private final ApiService apiServiceImpl;
    private final AsyncTaskExecutor taskExecutor;

    @Autowired
    public LocationServiceImpl(LocationDao locationDaoImpl, ApiService apiServiceImpl, AsyncTaskExecutor taskExecutor) {
        this.locationDaoImpl = locationDaoImpl;
        this.apiServiceImpl = apiServiceImpl;
        this.taskExecutor = taskExecutor;
    }

    @Override
    @Cacheable(value = "weather", key = "#user.userId")
    public CompletableFuture<List<CardLocationDto>> getAllWeathers(CurrentUserDto user, List<Location> locations) {
        return CompletableFuture.supplyAsync(() -> getCardLocationDto(locations), taskExecutor);
    }

    @Override
    @Cacheable(value = "locations", key = "#user.userId")
    public List<Location> getLocations(CurrentUserDto user) {
        try {
            return locationDaoImpl.getLocationsByUserId(user.getUserId());
        } catch (DataAccessException e) {
            throw new GetLocationsByUserIdException(e);
        }
    }

    @Override
    public ResponseWithCoordinates[] findAllLocationsByName(String locationName) {
        try {
            return apiServiceImpl.findAllLocations(locationName);
        } catch (RestClientException e) {
            throw new ConnectToWeatherServiceException(e);
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "#user.userId"),
            @CacheEvict(value = "weather", key = "#user.userId")})
    public void saveNewLocation(UserLocationsDto location, CurrentUserDto user) {
        try {
            Location newLocation = new Location(
                    user.getLogin(), user.getUserId(),
                    Double.valueOf(location.getLatitude()),
                    Double.valueOf(location.getLongitude()));
            locationDaoImpl.saveLocation(newLocation);
        } catch (DuplicateKeyException e) {
            throw new LocationAlreadyExist(e);
        } catch (DataAccessException ex) {
            throw new SaveLocationException(ex);
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "#user.userId"),
            @CacheEvict(value = "weather", key = "#user.userId")})
    public void deleteLocation(CurrentUserDto user, String latitude, String longitude) {
        double latitudeDouble;
        double longitudeDouble;
        try {
            latitudeDouble = Double.parseDouble(latitude);
            longitudeDouble = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            throw new ParseCoordinatesException(e);
        }
        try {
            locationDaoImpl.deleteLocation(user.getUserId(), latitudeDouble, longitudeDouble);
        } catch (DataAccessException e) {
            throw new DeleteLocationException(e);
        }
    }

    private List<CardLocationDto> getCardLocationDto(List<Location> locations) {
        List<CardLocationDto> cardLocations = new ArrayList<>();

        for (Location location : locations) {
            ResponseWithWeatherDto weather;
            try {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                weather = apiServiceImpl.getWeather(latitude, longitude);
            } catch (RestClientException e) {
                throw new ConnectToWeatherServiceException(e);
            }
            cardLocations.add(new CardLocationDto(
                    weather.getWeather().get(0).getIcon(),
                    weather.getMain().getTemp(),
                    location.getName(),
                    weather.getSys().getCountry(),
                    weather.getMain().getFeelsLike(),
                    weather.getWeather().get(0).getDescription(),
                    weather.getMain().getHumidity(),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude())));
        }
        return cardLocations;
    }
}
