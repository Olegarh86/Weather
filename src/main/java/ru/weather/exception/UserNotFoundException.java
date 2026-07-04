package ru.weather.exception;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("An error occurred while trying to retrieve a user from the database");
    }
}
