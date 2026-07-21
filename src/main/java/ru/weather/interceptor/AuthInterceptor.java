package ru.weather.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.weather.exception.SessionNotFound;
import ru.weather.service.SessionService;
import ru.weather.service.UserProfileService;

public class AuthInterceptor implements HandlerInterceptor {
    private final SessionService sessionServiceImpl;
    private final UserProfileService userProfileServiceImpl;

    @Autowired
    public AuthInterceptor(SessionService sessionServiceImpl, UserProfileService userProfileServiceImpl) {
        this.sessionServiceImpl = sessionServiceImpl;
        this.userProfileServiceImpl = userProfileServiceImpl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.endsWith("/users/sign-up") || uri.endsWith("/users/sign-in") || uri.endsWith("/users") ||
            uri.endsWith("/users/login") || uri.endsWith("/users/error") || uri.contains("/resources/")) {
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
                Long userId = sessionServiceImpl.getUserIdAndRefreshSession(token);
                String login = userProfileServiceImpl.getLogin(userId);
                request.setAttribute("userId", userId);
                request.setAttribute("login", login);
                return true;
            } catch (SessionNotFound e) {
                response.sendRedirect(request.getContextPath() + "/users/sign-in");
                return false;
            }
        }
        response.sendRedirect(request.getContextPath() + "/users/sign-in");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && request.getAttribute("login") != null) {
            modelAndView.addObject("login", request.getAttribute("login"));
        }
    }
}
