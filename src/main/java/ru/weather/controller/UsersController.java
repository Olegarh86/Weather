package ru.weather.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.weather.dto.*;
import ru.weather.exception.LoginAlreadyExist;
import ru.weather.exception.PasswordIncorrectException;
import ru.weather.exception.UserNotFoundException;
import ru.weather.service.*;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/weather/users")
public class UsersController {
    private final AuthService authService;
    private final PasswordEncoderService passwordEncoderService;
    private final UserProfileService userProfileService;
    private final SessionService sessionService;
    private final LocationService locationService;

    @Autowired
    public UsersController(AuthService authService, PasswordEncoderService passwordEncoderService, UserProfileService userProfileService, SessionService sessionService, LocationService locationService) {
        this.authService = authService;
        this.passwordEncoderService = passwordEncoderService;
        this.userProfileService = userProfileService;
        this.sessionService = sessionService;
        this.locationService = locationService;
    }

    @GetMapping
    public String users() {
        return "redirect:/weather/users/index";
    }

    @PostMapping
    public String signUp(@RequestParam("redirect_to") String redirectTo,
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
        String password = passwordEncoderService.encodePassword(userSignUpDto);
        userSignUpDto.setPassword(password);
        try {
            userProfileService.createNewUser(userSignUpDto);
        } catch (LoginAlreadyExist e) {
            log.warn(e.getMessage(), e);
            bindingResult.rejectValue("login", "AlreadyExist.weatherUser.login");
            return "sign-up-with-errors";
        }
        return "redirect:" + redirectTo;
    }

    @GetMapping("/sign-up")
    public String signUp(@ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto) {
        return "sign-up";
    }

    @GetMapping("/sign-in")
    public String signing(@ModelAttribute("userDto") UserDto userDto) {
        return "sign-in";
    }

    @PostMapping("/sign-out")
    public String signOut(@CookieValue(value = "uuid", required = false) String token, HttpServletResponse response) {
        sessionService.deleteSession(token);
        Cookie cookie = authService.cleanUpCookie();
        response.addCookie(cookie);
        return "redirect:/weather/users/index";
    }

    @PostMapping("/login")
    public String getUser(@RequestParam("redirect_to") String redirectTo, @ModelAttribute("userSignInDto") @Valid UserDto userDto,
                          BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "sign-in-with-errors";
        }
        Cookie cookie;
        try {
            cookie = authService.authenticate(userDto);
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
        return "redirect:" + redirectTo;
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        Long userId = (Long) request.getAttribute("userId");
        List<CardLocationDto> cardLocations = locationService.getAllWeathers(userId);
        model.addAttribute("allLocations", cardLocations);
        return "index";
    }

    @GetMapping("/search-results")
    public String searchResults(@RequestParam("location") String locationName, Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ResponseWithCoordinates[] allLocations = locationService.findAllLocationsByName(locationName);
        model.addAttribute("id", userId);
        model.addAttribute("locationName", locationName);
        model.addAttribute("allLocations", allLocations);
        return "search-results";
    }

    @PostMapping("/locations/add")
    public String addLocation(UserLocationsDto location) {
        locationService.saveNewLocation(location);
        return "redirect:/weather/users/index";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(@RequestParam("latitude") String latitude,
                                 @RequestParam("longitude") String longitude, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        locationService.deleteLocation(userId, latitude, longitude);
        return "redirect:/weather/users/index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
