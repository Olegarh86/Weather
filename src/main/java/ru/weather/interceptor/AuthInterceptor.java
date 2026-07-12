package ru.weather.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.weather.exception.SessionNotFound;
import ru.weather.service.SessionService;
import ru.weather.service.UserProfileService;

public class AuthInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;
    private final UserProfileService userProfileService;

    @Autowired
    public AuthInterceptor(SessionService sessionService, UserProfileService userProfileService) {
        this.sessionService = sessionService;
        this.userProfileService = userProfileService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if ("/weather/users/sign-up".equals(uri) || "/weather/users/sign-in".equals(uri) ||
            "/weather/users/login".equals(uri) || "/weather/users/error".equals(uri) || uri.startsWith("/resources")) {
            return true;
        }
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("uuid".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token != null) {
            try {
                Long userId = sessionService.getUserIdAndRefreshSession(token);
                String login = userProfileService.getLogin(userId);
                request.setAttribute("userId", userId);
                request.setAttribute("login", login);
                return true;
            } catch (SessionNotFound e) {
                response.sendRedirect(request.getContextPath() + "/weather/users/sign-in");
                return false;
            }
        }
        response.sendRedirect(request.getContextPath() + "/weather/users/sign-in");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.getAttribute("login") != null) {
            modelAndView.addObject("login", request.getAttribute("login"));
        }
    }
}
