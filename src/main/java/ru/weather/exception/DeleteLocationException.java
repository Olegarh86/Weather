package ru.weather.exception;

import org.springframework.dao.DataAccessException;

public class DeleteLocationException extends RuntimeException {
    public DeleteLocationException(DataAccessException e) {
        super("In the process of deleting a location, an exception occurred", e);
    }
}
