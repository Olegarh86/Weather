package ru.weather.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.weather.model.WeatherLocation;

import java.util.List;

@Repository
public class LocationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WeatherLocation> getLocationsByUserId(long userId) {
        return jdbcTemplate.query("SELECT * FROM locations WHERE user_id = ?", new Object[]{userId},
                new BeanPropertyRowMapper<>(WeatherLocation.class));
    }
}
