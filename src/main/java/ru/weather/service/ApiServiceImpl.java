package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {
    private final RestTemplate restTemplate;
    @Value("${weather.findLocationsUrl}")
    private String urlFindCoordinates;
    @Value("${weather.getWeatherUrl}")
    private String urlGetWeather;
    @Value("${weather.apiKey1}")
    private String apiKey;
    @Value("${weather.units}")
    private String units;
    @Value("${weather.limit}")
    private String limit;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWithWeatherDto getWeather(String latitude, String longitude) {
        String path = urlGetWeather + "?lat={latitude}&lon={longitude}&units={units}&appid={appid}";
        Map<String, String> params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("units", units);
        params.put("appid", apiKey);
        return restTemplate.getForObject(path, ResponseWithWeatherDto.class, params);
    }

    public ResponseWithCoordinates[] findAllLocations(String name) {
        String path = urlFindCoordinates + "?q={name}&limit={limit}&appid={appid}";
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("limit", limit);
        params.put("appid", apiKey);
        return restTemplate.getForObject(path, ResponseWithCoordinates[].class, params);
    }
}
