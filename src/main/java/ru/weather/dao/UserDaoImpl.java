package ru.weather.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.weather.model.WeatherUser;

import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(@Lazy JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<WeatherUser> getUser(String login, String password) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE login=? AND password=?",
                new Object[]{login, password}, new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }

    @Override
    public void saveUser(WeatherUser user) {
        jdbcTemplate.update("INSERT INTO WeatherUsers (login,password) VALUES (?,?)", user.getLogin(), user.getPassword());
    }

    @Override
    public Optional<WeatherUser> findByLogin(String login) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE login=?", new Object[]{login},
                new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }

    @Override
    public Optional<WeatherUser> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM WeatherUsers WHERE id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(WeatherUser.class)).stream().findFirst();
    }
}
