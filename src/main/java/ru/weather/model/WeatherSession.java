package ru.weather.model;

import java.util.UUID;

public record WeatherSession(UUID uuid, Long userId) {
}
