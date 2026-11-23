package ru.weather.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.weather.dto.UserDto;
import ru.weather.dto.UserSignUpDto;
import ru.weather.exception.LoginAlreadyExist;
import ru.weather.exception.PasswordIncorrectException;
import ru.weather.exception.UserNotFoundException;
import ru.weather.service.AuthService;
import ru.weather.service.SessionService;
import ru.weather.service.UserProfileService;

@Slf4j
@Controller
public class AuthController {
    private final AuthService authServiceImpl;
    private final UserProfileService userProfileServiceImpl;
    private final SessionService sessionServiceImpl;

    @Autowired
    public AuthController(AuthService authServiceImpl,
                          UserProfileService userProfileServiceImpl,
                          SessionService sessionServiceImpl) {
        this.authServiceImpl = authServiceImpl;
        this.userProfileServiceImpl = userProfileServiceImpl;
        this.sessionServiceImpl = sessionServiceImpl;
    }

    @GetMapping
    public String getRoot() {
        return "redirect:/index";
    }

    @PostMapping("/sign-up")
    public String createNewUser(@RequestParam("redirect_to") String redirectTo,
                                @ModelAttribute("userSignUpDto") @Valid UserSignUpDto userSignUpDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-up-with-errors";
        }
        if (!userSignUpDto.getPassword().equals(userSignUpDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "NotConfirm.weatherUser.password");
            return "sign-up-with-errors";
        }
        if (userSignUpDto.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "Size.weatherUser.password");
            return "sign-up-with-errors";
        }
        try {
            userProfileServiceImpl.createNewUser(userSignUpDto);
        } catch (LoginAlreadyExist e) {
            log.warn(e.getMessage(), e);
            bindingResult.rejectValue("login", "AlreadyExist.weatherUser.login");
            return "sign-up-with-errors";
        }
        if (redirectTo == null || redirectTo.isBlank()) {
            redirectTo = "/index";
        }
        return "redirect:" + redirectTo;
    }

    @GetMapping("/sign-up")
    public String signUp(@ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto) {
        return "sign-up";
    }

    @GetMapping("/sign-in")
    public String signIn(@ModelAttribute("userDto") UserDto userDto) {
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String getUser(@RequestParam("redirect_to") String redirectTo,
                          @ModelAttribute("userSignInDto") @Valid UserDto userDto,
                          BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "sign-in-with-errors";
        }
        Cookie cookie;
        try {
            cookie = authServiceImpl.authenticate(userDto);
        } catch (UserNotFoundException e) {
            log.warn(e.getMessage(), e);
            bindingResult.rejectValue("login", "NotFound.weatherUser");
            return "sign-in-with-errors";
        } catch (PasswordIncorrectException ex) {
            log.warn(ex.getMessage(), ex);
            bindingResult.rejectValue("password", "Incorrect.weatherUser.password");
            return "sign-in-with-errors";
        }
        response.addCookie(cookie);
        if (redirectTo == null || redirectTo.isBlank()) {
            redirectTo = "/index";
        }
        return "redirect:" + redirectTo;
    }

    @PostMapping("/sign-out")
    public String signOut(@CookieValue(value = "uuid", required = false) String token, HttpServletResponse response) {
        sessionServiceImpl.deleteSession(token);
        Cookie cookie = authServiceImpl.cleanUpCookie();
        response.addCookie(cookie);
        return "redirect:/index";
    }
}
