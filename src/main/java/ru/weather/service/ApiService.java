package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;

@Service
public class ApiService {
    private final RestTemplate restTemplate;
    private final String apiKey = "45f8bbe2af51e7569b70704e6c9a7692";

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWithWeatherDto getWeather(String name) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + name + "&units=metric&appid=" + apiKey;
        return restTemplate.getForObject(url, ResponseWithWeatherDto.class);
    }

    private ResponseWithCoordinates getCoordinates(String name) {
        String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + name + "&appid=" + apiKey;
        return restTemplate.getForObject(url, ResponseWithCoordinates.class);
    }
}
