package ru.weather.exception;

public class GetLocationsByUserIdException extends RuntimeException {
    public GetLocationsByUserIdException() {
        super("Could not get all coordinates by user id from database");
    }
}
