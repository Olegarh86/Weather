package ru.weather.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WeatherUser {
    @Setter
    private long id;
    @Setter
    @NotEmpty(message = "Login should not be empty")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    private String login;
    @Setter
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, max = 64, message = "Password should be between 8 and 64 characters")
    private String password;
    private List<WeatherLocation> locations;

    public WeatherUser() {
    }

    public WeatherUser(String login, String password) {
        this.login = login;
        this.password = password;
        this.locations =  new ArrayList<>();

    }

    public void addLocation(WeatherLocation location) {
        locations.add(location);
    }
}
