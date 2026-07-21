package ru.weather.dao;

import ru.weather.model.WeatherSession;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionDao {
    Optional<Long> getUserIdAndRefreshSession(Instant expiresAt, UUID uuid, Instant now);

    void addSession(WeatherSession weatherSession, Instant expiresAt);

    void deleteSession(UUID uuid);

    Long getSession(UUID uuid);
}
