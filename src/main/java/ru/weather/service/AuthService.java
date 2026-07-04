package ru.weather.service;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.weather.dto.UserDto;
import ru.weather.exception.PasswordIncorrectException;

import java.util.UUID;

@Service
public class AuthService {
    private final UserProfileService userProfileService;
    private final PasswordEncoderService passwordEncoderService;
    private final SessionService sessionService;

    @Autowired
    public AuthService(UserProfileService userProfileService, PasswordEncoderService passwordEncoderService,
                       SessionService sessionService) {
        this.userProfileService = userProfileService;
        this.passwordEncoderService = passwordEncoderService;
        this.sessionService = sessionService;
    }

    public Cookie authenticate(UserDto userDto) {
        String passwordFromDataBase = userProfileService.getPassword(userDto);

        if (passwordEncoderService.matches(userDto.getPassword(), passwordFromDataBase)) {
            Long id = userProfileService.getUserId(userDto);
            UUID uuid = UUID.randomUUID();
            Cookie cookie = createNewCookie(uuid);
            sessionService.createNewSession(uuid, id);
            return cookie;
        }
        throw new PasswordIncorrectException();
    }

    private Cookie createNewCookie(UUID uuid) {
        Cookie cookie = new Cookie("uuid", uuid.toString());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie cleanUpCookie() {
        Cookie cookie = new Cookie("uuid", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
