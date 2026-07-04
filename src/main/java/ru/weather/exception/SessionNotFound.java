package ru.weather.exception;

public class SessionNotFound extends RuntimeException {
    public SessionNotFound() {
        super("Session not found");
    }
}
