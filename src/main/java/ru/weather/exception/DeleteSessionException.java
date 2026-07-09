package ru.weather.exception;

public class DeleteSessionException extends RuntimeException {
    public DeleteSessionException(Exception e) {
        super("An error occurred in the process of deleting the session", e);
    }
}
