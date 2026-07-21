package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.weather.dao.LocationDao;
import ru.weather.dao.LocationDaoImpl;
import ru.weather.dto.CardLocationDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;
import ru.weather.dto.UserLocationsDto;
import ru.weather.exception.*;
import ru.weather.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LocationService {
    private final LocationDao locationDaoImpl;
    private final ApiService apiService;
    private final AsyncTaskExecutor taskExecutor;

    @Autowired
    public LocationService(LocationDao locationDaoImpl, ApiService apiService, AsyncTaskExecutor taskExecutor) {
        this.locationDaoImpl = locationDaoImpl;
        this.apiService = apiService;
        this.taskExecutor = taskExecutor;
    }

    @Cacheable(value = "weather", key = "#userId")
    public CompletableFuture<List<CardLocationDto>> getAllWeathers(Long userId, List<Location> locations) {
        return CompletableFuture.supplyAsync(() -> getCardLocationDto(locations), taskExecutor);
    }

    @Cacheable(value = "locations", key = "#userId")
    public List<Location> getLocations(Long userId) {
        try {
            return locationDaoImpl.getLocationsByUserId(userId);
        } catch (DataAccessException e) {
            throw new GetLocationsByUserIdException(e);
        }
    }

    public List<CardLocationDto> getCardLocationDto(List<Location> locations) {
        List<CardLocationDto> cardLocations = new ArrayList<>();

        for (Location location : locations) {
            ResponseWithWeatherDto weather;
            try {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                weather = apiService.getWeather(latitude, longitude);
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

    public ResponseWithCoordinates[] findAllLocationsByName(String locationName) {
        try {
            return apiService.findAllLocations(locationName);
        } catch (RestClientException e) {
            throw new ConnectToWeatherServiceException(e);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "T(Long).valueOf(#location.userId)"),
            @CacheEvict(value = "weather", key = "T(Long).valueOf(#location.userId)")})
    public void saveNewLocation(UserLocationsDto location) {
        try {
            Location newLocation = new Location(
                    location.getName(),
                    Long.valueOf(location.getUserId()),
                    Double.valueOf(location.getLatitude()),
                    Double.valueOf(location.getLongitude()));
            locationDaoImpl.saveLocation(newLocation);
        } catch (NumberFormatException e) {
            throw new SaveLocationException(e);
        } catch (DataAccessException ex) {
            throw new LocationAlreadyExist(ex);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "#userId"),
            @CacheEvict(value = "weather", key = "#userId")})
    public void deleteLocation(Long userId, String latitude, String longitude) {
        double latitudeDouble;
        double longitudeDouble;
        try {
            latitudeDouble = Double.parseDouble(latitude);
            longitudeDouble = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            throw new ParseCoordinatesException(e);
        }
        try {
            locationDaoImpl.deleteLocation(userId, latitudeDouble, longitudeDouble);
        } catch (DataAccessException e) {
            throw new DeleteLocationException(e);
        }
    }
}
