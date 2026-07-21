package ru.weather.dao;

import ru.weather.model.WeatherUser;

import java.util.Optional;

public interface UserDao {
    Optional<WeatherUser> getUser(String login, String password);

    void saveUser(WeatherUser user);

    Optional<WeatherUser> findByLogin(String login);

    Optional<WeatherUser> findById(long id);
}
