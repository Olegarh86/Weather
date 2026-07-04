package ru.weather.exception;

public class SaveLocationException extends RuntimeException {
    public SaveLocationException(Exception e) {
        super("An exception occurred in the process of saving the location", e);
    }
}
