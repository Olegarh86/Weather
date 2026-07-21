package ru.weather.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.weather.model.Location;

import java.util.List;

@Repository
public class LocationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Location> getLocationsByUserId(long userId) {
        return jdbcTemplate.query("SELECT * FROM locations WHERE user_id = ? ORDER BY id DESC", new Object[]{userId},
                new BeanPropertyRowMapper<>(Location.class));
    }

    public void saveLocation(Location location) {
        jdbcTemplate.update("INSERT INTO locations (name, user_id, latitude, longitude) VALUES (?,?,?,?)",
                location.getName(), location.getUserId(), location.getLatitude(), location.getLongitude());
    }

    public void deleteLocation(Long userId, Double latitude, Double longitude) {
        jdbcTemplate.update("DELETE FROM locations WHERE user_id = ? AND latitude = ? AND longitude = ?",
                userId,
                latitude,
                longitude);
    }
}
