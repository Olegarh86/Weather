package ru.weather.exception;

public class DeleteSessionException extends RuntimeException {
    public DeleteSessionException() {
        super("An error occurred in the process of deleting the session");
    }
}
