package ru.weather.exception;

public class ParseCoordinatesException extends RuntimeException {
    public ParseCoordinatesException(NumberFormatException e) {
        super("An exception occurred during the parsing of coordinates", e);
    }
}
