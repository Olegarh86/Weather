package ru.weather.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.weather.exception.SessionNotFound;
import ru.weather.service.SessionService;

public class AuthInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;

    @Autowired
    public AuthInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("uuid")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token != null) {
            try {
                Long userId = sessionService.getUserIdAndRefreshSession(token);
                request.setAttribute("userId", userId);
                return true;
            } catch (SessionNotFound e) {
                response.sendRedirect(request.getContextPath() + "/weather/users/sign-in");
                return false;
            }
        }
        response.sendRedirect(request.getContextPath() + "/weather/users/sign-in");
        return false;
    }
}
