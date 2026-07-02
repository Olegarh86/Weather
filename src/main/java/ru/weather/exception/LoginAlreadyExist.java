package ru.weather.exception;

import org.springframework.dao.DataAccessException;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(DataAccessException ex) {
        super(ex);
    }
}
