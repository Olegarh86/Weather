package ru.weather.service;

import ru.weather.dto.UserSignUpDto;

public interface PasswordEncoderService {
    String encodePassword(UserSignUpDto userSignUpDto);

    boolean matches(String password, String password1);
}
