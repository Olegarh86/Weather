package ru.weather.service;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.weather.dto.UserDto;
import ru.weather.exception.PasswordIncorrectException;

import java.util.UUID;

@Service
public class AuthService {
    private final String cookieKey = "uuid";
    private final String cookiePath = "/";
    private final boolean httpOnly = true;
    private final UserProfileService userProfileService;
    private final PasswordEncoderService passwordEncoderService;
    private final SessionService sessionService;
    @Value("${cookies.maxAge}")
    private int maxAge;

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
        Cookie cookie = new Cookie(cookieKey, uuid.toString());
        cookie.setMaxAge(maxAge);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    public Cookie cleanUpCookie() {
        Cookie cookie = new Cookie(cookieKey, "");
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }
}
