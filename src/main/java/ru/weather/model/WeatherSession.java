package ru.weather.model;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
public class WeatherSession {
    private UUID uuid;
    private long userId;
    private Timestamp timestamp;

    public WeatherSession(UUID uuid, long userId) {
        this.uuid = uuid;
        this.userId = userId;
    }
}
