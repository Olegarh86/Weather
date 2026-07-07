package ru.weather.exception;

public class ConnectToWeatherServiceException extends RuntimeException {
    public ConnectToWeatherServiceException(Exception cause) {
        super("Could not connect to weather service", cause);
    }
}
