package ru.weather.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WeatherSession {
    private final UUID uuid;
    private final Long userId;

    public WeatherSession(UUID uuid, Long userId) {
        this.uuid = uuid;
        this.userId = userId;
    }
}
