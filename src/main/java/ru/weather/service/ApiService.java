package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.dto.ResponseWithWeatherDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {
    private final RestTemplate restTemplate;
    private final String apiKey1 = "45f8bbe2af51e7569b70704e6c9a7692";
    private final String apiKey2 = "700d12f1841ae09c67233aae0047d894";
    private final String apiKey3 = "5915d1068f24ea47c9e69ed8f5476f5b";
    private final String apiKey4 = "c646e1486eb8bee3ad2bb7bf9a4d451a";
    private final String language = "&lang=ru";
    private final String units = "&units=metric";
    private final String limit = "&limit=5";

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseWithWeatherDto getWeather(String latitude, String longitude) {
        String urlWeather =
                "https://api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&units=metric&appid={appid}";
        Map< String, String > params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("appid", apiKey3);
        return restTemplate.getForObject(urlWeather, ResponseWithWeatherDto.class, params);
    }

//    public ResponseWithWeatherDto getWeather(String latitude, String longitude) {
//        String path =
//                "https://api.openweathermap.org/data/2.5/weather?lat=" +
//                latitude + "&lon=" + longitude + "&units=metric" + "&appid=" + apiKey1;
//        return restTemplate.getForObject(path, ResponseWithWeatherDto.class);
//    }

    public ResponseWithCoordinates[] findAllLocations(String name) {
        Map< String, String > params = new HashMap<>();
        params.put("name", name);
        params.put("limit", "5");
        params.put("appid", apiKey3);
        String urlFindCoordinates = "https://api.openweathermap.org/geo/1" +
                                    ".0/direct?q={name}&limit={limit}&appid={appid}";
        return restTemplate.getForObject(urlFindCoordinates, ResponseWithCoordinates[].class, params);
    }
}
