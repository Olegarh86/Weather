package ru.weather.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.weather.dao.LocationDao;
import ru.weather.dao.SessionDao;
import ru.weather.dao.UserDao;
import ru.weather.dto.*;
import ru.weather.exception.*;
import ru.weather.service.*;

import java.util.List;


@Controller
@RequestMapping("/weather/users")
public class UsersController {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final LocationDao locationDao;
    private final ApiService apiService;
    private final AuthService authService;
    private final PasswordEncoderService passwordEncoderService;
    private final UserProfileService userProfileService;
    private final SessionService sessionService;
    private final LocationService locationService;

    @Autowired
    public UsersController(UserDao userDao, SessionDao sessionDao, LocationDao locationDao, ApiService apiService, AuthService authService, PasswordEncoderService passwordEncoderService, UserProfileService userProfileService, SessionService sessionService, LocationService locationService) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.locationDao = locationDao;
        this.apiService = apiService;
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
    public String signUp(@RequestParam("redirect_to") String redirectTo, @ModelAttribute("userSignUpDto") @Valid UserSignUpDto userSignUpDto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-up-with-errors";
        }
        String password = passwordEncoderService.encodePassword(userSignUpDto);

        if (password.isBlank()) {
            bindingResult.rejectValue("passwordConfirm", "passwordNotConfirm");
            return "sign-up-with-errors";
        }
        userSignUpDto.setPassword(password);
        try {
            userProfileService.createNewUser(userSignUpDto);
        } catch (LoginAlreadyExist e) {
            bindingResult.rejectValue("login", "loginExist");
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
        if (token == null) {
            return "redirect:/weather/users/sign-in";
        }
        try {
            sessionService.deleteSession(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        Cookie cookie = authService.authenticate(userDto);
        response.addCookie(cookie);
        return "redirect:" + redirectTo;
    }

    @GetMapping("/index")
    public String index(@CookieValue(value = "uuid", required = false) String token, Model model) {
        if (token == null) {
            return "redirect:/weather/users/sign-in";
        }
        Long userId;
        try {
            userId = sessionService.getUserIdAndRefreshSession(token);
        } catch (ParseUuidException | SessionNotFound e) {
            return "redirect:/weather/users/sign-in";
        }
        String login;
        try {
            login = userProfileService.getLogin(userId);
        } catch (UserNotFoundException e) {
            return "redirect:/weather/users/sign-in";
        }
        List<CardLocationDto> cardLocations;
        try {
            cardLocations = locationService.getAllWeathers(userId);
        } catch (GetLocationsByUserIdException | ConnectToWeatherServiceException e) {
            return "redirect:/weather/users/error";
        }
        model.addAttribute("login", login);
        model.addAttribute("allLocations", cardLocations);
        return "index";
    }

    @GetMapping("/search-results")
    public String searchResults(@CookieValue(value = "uuid", required = false) String token,
                                @RequestParam("location") String locationName, Model model) {
        if (token == null) {
            return "redirect:/weather/users/sign-in";
        }
        Long userId;
        try {
            userId = sessionService.getUserIdAndRefreshSession(token);
        } catch (ParseUuidException | SessionNotFound e) {
            return "redirect:/weather/users/sign-in";
        }
        String login;
        try {
            login = userProfileService.getLogin(userId);
        } catch (UserNotFoundException e) {
            return "redirect:/weather/users/sign-in";
        }
        ResponseWithCoordinates[] allLocations;
        try {
            allLocations = locationService.findAllLocationsByName(locationName);
        } catch (ConnectToWeatherServiceException e) {
            return "redirect:/weather/users/error";
        }
        model.addAttribute("login", login);
        model.addAttribute("id", userId);
        model.addAttribute("locationName", locationName);
        model.addAttribute("allLocations", allLocations);
        return "search-results";
    }

    @PostMapping("/locations/add")
    public String addLocation(@CookieValue(value = "uuid", required = false) String token,
                              UserLocationsDto location) {
        if (token == null) {
            return "redirect:/weather/users/sign-in";
        }
        try {
            locationService.saveNewLocation(location);
        } catch (SaveLocationException ignored) {
        }
        return "redirect:/weather/users/index";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(@CookieValue(value = "uuid", required = false) String token,
                                 @RequestParam("latitude") String latitude,
                                 @RequestParam("longitude") String longitude) {
        if (token == null) {
            return "redirect:/weather/users/sign-in";
        }
        Long userId;
        try {
            userId = sessionService.getUserIdAndRefreshSession(token);
        } catch (SessionNotFound e) {
            return "redirect:/weather/users/sign-in";
        }
        try {
            locationService.deleteLocation(userId, latitude, longitude);
        } catch (ParseCoordinatesException | DeleteLocationException e) {
            return "redirect:/weather/users/error";
        }
        return "redirect:/weather/users/index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
