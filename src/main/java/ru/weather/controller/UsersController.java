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
import ru.weather.exception.LocationAlreadyExist;
import ru.weather.exception.LoginAlreadyExist;
import ru.weather.exception.PasswordIncorrectException;
import ru.weather.exception.UserNotFoundException;
import ru.weather.model.Location;
import ru.weather.service.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Controller
@RequestMapping("/users")
public class UsersController {
    private final AuthService authServiceImpl;
    private final UserProfileService userProfileServiceImpl;
    private final SessionService sessionServiceImpl;
    private final LocationService locationServiceImpl;

    @Autowired
    public UsersController(AuthService authServiceImpl, UserProfileService userProfileServiceImpl,
                           SessionService sessionServiceImpl, LocationService locationServiceImpl) {
        this.authServiceImpl = authServiceImpl;
        this.userProfileServiceImpl = userProfileServiceImpl;
        this.sessionServiceImpl = sessionServiceImpl;
        this.locationServiceImpl = locationServiceImpl;
    }

    @GetMapping
    public String users() {
        return "redirect:/users/index";
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
        try {
            userProfileServiceImpl.createNewUser(userSignUpDto);
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
        sessionServiceImpl.deleteSession(token);
        Cookie cookie = authServiceImpl.cleanUpCookie();
        response.addCookie(cookie);
        return "redirect:/users/index";
    }

    @PostMapping("/login")
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
        return "redirect:" + redirectTo;
    }

    @GetMapping("/index")
    public CompletableFuture<String> index(HttpServletRequest request, Model model) {
        Long userId = (Long) request.getAttribute("userId");
        List<Location> locations = locationServiceImpl.getLocations(userId);
        CompletableFuture<List<CardLocationDto>> cardLocations = locationServiceImpl.getAllWeathers(userId, locations);
        model.addAttribute("allLocations", cardLocations);
        return cardLocations.thenApply(allLocations -> {
            model.addAttribute("allLocations", allLocations);
            return "index";
        });
    }

    @GetMapping("/search-results")
    public String searchResults(@RequestParam("location") String locationName, Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ResponseWithCoordinates[] allLocations = locationServiceImpl.findAllLocationsByName(locationName);
        model.addAttribute("id", userId);
        model.addAttribute("locationName", locationName);
        model.addAttribute("allLocations", allLocations);
        return "search-results";
    }

    @PostMapping("/locations/add")
    public String addLocation(UserLocationsDto location) {
        try {
            locationServiceImpl.saveNewLocation(location);
        } catch (LocationAlreadyExist e) {
            return "redirect:/users/index";
        }
        return "redirect:/users/index";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(@RequestParam("latitude") String latitude,
                                 @RequestParam("longitude") String longitude, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        locationServiceImpl.deleteLocation(userId, latitude, longitude);
        return "redirect:/users/index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
