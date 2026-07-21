package ru.weather.service;

import java.util.UUID;

public interface SessionService {
    Long getUserIdAndRefreshSession(String token);

    void deleteSession(String token);

    Long getSession(UUID uuid);

    void createNewSession(UUID uuid, Long id);
}
