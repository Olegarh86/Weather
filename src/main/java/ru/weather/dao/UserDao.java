package ru.weather.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.weather.model.WeatherUser;

import java.util.Optional;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<WeatherUser> getUser(String login, String password) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE login=? AND password=?",
                new Object[]{login, password}, new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }

    public void saveUser(WeatherUser user) {
        jdbcTemplate.update("INSERT INTO WeatherUsers (login,password) VALUES (?,?)", user.getLogin(), user.getPassword());
    }

    public Optional<WeatherUser> findByLogin(String login) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE login=?", new Object[]{login},
                new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }

    public Optional<WeatherUser> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }
}
