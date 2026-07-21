package ru.weather.exception;

import org.springframework.dao.DataAccessException;

public class LocationAlreadyExist extends RuntimeException {
    public LocationAlreadyExist(DataAccessException ex) {
        super("This location was already added ", ex);
    }
}
