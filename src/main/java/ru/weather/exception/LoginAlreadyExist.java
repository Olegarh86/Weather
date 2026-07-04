package ru.weather.exception;

import org.springframework.dao.DataAccessException;

public class LoginAlreadyExist extends RuntimeException {
    public LoginAlreadyExist(DataAccessException ex) {
        super("This login is already taken, enter another login", ex);
    }
}
