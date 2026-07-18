package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import ru.weather.dao.LocationDao;
import ru.weather.dto.CardLocationDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;
import ru.weather.dto.UserLocationsDto;
import ru.weather.exception.*;
import ru.weather.model.WeatherLocation;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    private final LocationDao locationDao;
    private final ApiService apiService;

    @Autowired
    public LocationService(LocationDao locationDao, ApiService apiService) {
        this.locationDao = locationDao;
        this.apiService = apiService;
    }

    public List<CardLocationDto> getAllWeathers(Long userId) {
        List<WeatherLocation> locations;
        try {
            locations = locationDao.getLocationsByUserId(userId);
        } catch (DataAccessException e) {
            throw new GetLocationsByUserIdException(e);
        }
        List<CardLocationDto> cardLocations = new ArrayList<>();

        for (WeatherLocation location : locations) {
            ResponseWithWeatherDto weather;
            try {
                weather = apiService.getWeather(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
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

    public void saveNewLocation(UserLocationsDto location) {
        try {
            WeatherLocation weatherLocation = new WeatherLocation(
                    location.getName(),
                    Long.valueOf(location.getUserId()),
                    Double.valueOf(location.getLatitude()),
                    Double.valueOf(location.getLongitude()));
            locationDao.saveLocation(weatherLocation);
        } catch (NumberFormatException e) {
            throw new SaveLocationException(e);
        } catch (DataAccessException ignore) {
        }
    }

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
            locationDao.deleteLocation(userId, latitudeDouble, longitudeDouble);
        } catch (DataAccessException e) {
            throw new DeleteLocationException(e);
        }
    }
}
