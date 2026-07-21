package ru.weather.service;

import jakarta.servlet.http.Cookie;
import ru.weather.dto.UserDto;

public interface AuthService {
    Cookie authenticate(UserDto userDto);

    Cookie cleanUpCookie();
}
