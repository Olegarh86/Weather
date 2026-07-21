package ru.weather.service;

import jakarta.validation.Valid;
import ru.weather.dto.UserDto;
import ru.weather.dto.UserSignUpDto;

public interface UserProfileService {
    void createNewUser(@Valid UserSignUpDto userSignUpDto);

    String getPassword(UserDto userDto);

    Long getUserId(UserDto userDto);

    String getLogin(Long userId);
}
