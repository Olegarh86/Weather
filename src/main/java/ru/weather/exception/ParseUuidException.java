package ru.weather.exception;

public class ParseUuidException extends RuntimeException {
    public ParseUuidException(Exception e) {
        super("An error occurred while trying to parse the UUID", e);
    }
}
