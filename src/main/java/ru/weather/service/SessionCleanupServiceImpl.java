package ru.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SessionCleanupServiceImpl implements SessionCleanupService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionCleanupServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        jdbcTemplate.update("DELETE FROM sessions WHERE expires_at < clock_timestamp()");
    }
}
