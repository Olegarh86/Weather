package ru.weather.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.weather.exception.SessionNotFound;
import ru.weather.model.WeatherSession;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionDao(@Lazy JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> getUserIdAndRefreshSession(Instant expiresAt, UUID uuid, Instant now) {
        try {
            return Optional.of(jdbcTemplate.queryForObject("""
                            UPDATE sessions
                            SET expires_at = ?
                            WHERE id = ?
                              AND expires_at > ?
                              RETURNING user_id"""
                    , Long.class, Timestamp.from(expiresAt), uuid, Timestamp.from(now)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addSession(WeatherSession weatherSession, Instant expiresAt) {
        jdbcTemplate.update("INSERT INTO sessions (id,user_id, expires_at) VALUES (?,?,?)",
                weatherSession.uuid(),
                weatherSession.userId(),
                Timestamp.from(expiresAt));
    }

    public void deleteSession(UUID uuid) {
        jdbcTemplate.update("DELETE FROM sessions WHERE id = ?", uuid);
    }

    public Long getSession(UUID uuid) {
        try {
            return jdbcTemplate.queryForObject("""
                    SELECT sessions.user_id
                    FROM sessions
                    WHERE id = ?"""
                    , Long.class, uuid);
        } catch (EmptyResultDataAccessException e) {
            throw new SessionNotFound();
        }
    }
}