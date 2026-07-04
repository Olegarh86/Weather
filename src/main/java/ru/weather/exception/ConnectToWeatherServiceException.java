package ru.weather.exception;

public class ConnectToWeatherServiceException extends RuntimeException {
    public ConnectToWeatherServiceException() {
        super("Could not connect to weather service");
    }
}
