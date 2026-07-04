package ru.weather.exception;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException() {
        super("Password Incorrect.");
    }
}
