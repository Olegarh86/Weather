package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.weather.dao.SessionDao;
import ru.weather.exception.DeleteSessionException;
import ru.weather.exception.ParseUuidException;
import ru.weather.exception.SessionNotFound;
import ru.weather.model.WeatherSession;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionDao sessionDao;
    @Value("${cookies.maxAge}")
    private int maxAge;

    @Autowired
    public SessionService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void createNewSession(UUID uuid, Long id) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(maxAge);
        sessionDao.addSession(new WeatherSession(uuid, id), expiresAt);
    }

    public Long getUserIdAndRefreshSession(String token) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(maxAge);
        UUID uuid = parseUuid(token);
        Optional<Long> userIdOptional = sessionDao.getUserIdAndRefreshSession(expiresAt, uuid, now);
        if (userIdOptional.isEmpty()) {
            throw new SessionNotFound();
        }
        return userIdOptional.get();
    }

    public void deleteSession(String token) {
        UUID uuid = parseUuid(token);
        try {
            sessionDao.deleteSession(uuid);
        } catch (DataAccessException e) {
            throw new DeleteSessionException(e);
        }
    }

    private static UUID parseUuid(String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new ParseUuidException(e);
        }
        return uuid;
    }

    public Long getSession(UUID uuid) {
        Long id = sessionDao.getSession(uuid);
        if (id == null) {
            throw new SessionNotFound();
        }
        return id;
    }
}
