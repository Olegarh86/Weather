package ru.weather.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
public class WeatherUser {
    @Setter
    private long id;
    @Setter
    @NotEmpty(message = "{NotEmpty.weatherUser.login}")
    @Size(min = 2, max = 20, message = "{Size.weatherUser.login}")
    private String login;
    @Setter
    @NotEmpty(message = "{NotEmpty.weatherUser.password}")
    @Size(min = 8, max = 64, message = "{Size.weatherUser.password}")
    private String password;

    public WeatherUser() {
    }

    public WeatherUser(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
