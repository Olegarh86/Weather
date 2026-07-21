package ru.weather.service;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.weather.dto.UserDto;
import ru.weather.exception.PasswordIncorrectException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final String cookieKey = "uuid";
    private final String cookiePath = "/";
    private final boolean httpOnly = true;
    private final UserProfileService userProfileServiceImpl;
    private final PasswordEncoderService passwordEncoderServiceImpl;
    private final SessionService sessionServiceImpl;
    @Value("${cookies.maxAge}")
    private int maxAge;

    @Autowired
    public AuthServiceImpl(UserProfileService userProfileServiceImpl, PasswordEncoderService passwordEncoderServiceImpl,
                           SessionService sessionServiceImpl) {
        this.userProfileServiceImpl = userProfileServiceImpl;
        this.passwordEncoderServiceImpl = passwordEncoderServiceImpl;
        this.sessionServiceImpl = sessionServiceImpl;
    }

    @Override
    public Cookie authenticate(UserDto userDto) {
        String passwordFromDataBase = userProfileServiceImpl.getPassword(userDto);

        if (passwordEncoderServiceImpl.matches(userDto.getPassword(), passwordFromDataBase)) {
            Long id = userProfileServiceImpl.getUserId(userDto);
            UUID uuid = UUID.randomUUID();
            Cookie cookie = createNewCookie(uuid);
            sessionServiceImpl.createNewSession(uuid, id);
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

    @Override
    public Cookie cleanUpCookie() {
        Cookie cookie = new Cookie(cookieKey, "");
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }
}
