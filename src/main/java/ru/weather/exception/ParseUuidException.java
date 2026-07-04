package ru.weather.exception;

public class ParseUuidException extends RuntimeException {
    public ParseUuidException() {
        super("An error occurred while trying to parse the UUID");
    }
}
