package ru.weather.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.weather.dao.LocationDao;
import ru.weather.dao.SessionDao;
import ru.weather.dao.UserDao;
import ru.weather.dto.*;
import ru.weather.model.WeatherLocation;
import ru.weather.model.WeatherSession;
import ru.weather.model.WeatherUser;
import ru.weather.service.ApiService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final LocationDao locationDao;
    private final ApiService apiService;

    @Autowired
    public UsersController(UserDao userDao, SessionDao sessionDao, LocationDao locationDao, ApiService apiService) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.locationDao = locationDao;
        this.apiService = apiService;
    }

    @GetMapping
    public String users(Model model) {
        return "redirect:users/sign-in";
    }

    @PostMapping
    public String signUp(@ModelAttribute("userSignUpDto") @Valid UserSignUpDto userSignUpDto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "sign-up-with-errors";
        }
        if (userSignUpDto.getPassword().equals(userSignUpDto.getPasswordConfirm())) {
            try {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(userSignUpDto.getPassword());
                WeatherUser weatherUser = new WeatherUser(userSignUpDto.getLogin(), encodedPassword);
                userDao.saveUser(weatherUser);
                Optional<WeatherUser> weatherUserOptional = userDao.getUser(weatherUser.getLogin(), encodedPassword);

                if (weatherUserOptional.isPresent()) {
                    model.addAttribute("weatherUser", weatherUserOptional.get());
                }
                return "redirect:/users/sign-in";
            } catch (Exception e) {
                bindingResult.reject("password", "Passwords do not match");
                return "sign-up-with-errors";
            }
        }
        return "sign-up-with-errors";
    }

    @GetMapping("/sign-in")
    public String signing(@ModelAttribute("userDto") UserDto userDto) {
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String user(@ModelAttribute("weatherUser") @Valid WeatherUser weatherUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-in-with-errors";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(weatherUser.getPassword());
        Optional<WeatherUser> weatherUserOptional = userDao.getUser(weatherUser.getLogin(), encodedPassword);
        if (weatherUserOptional.isPresent()) {
            return "redirect:/users/index";
        }
        return "sign-in-with-errors";
    }

    @PostMapping("/login")
    public String getUser(@ModelAttribute("userSignInDto") @Valid UserDto userDto,
                          BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "sign-in-with-errors";
        }
        Optional<WeatherUser> optionalWeatherUser = userDao.findByLogin(userDto.getLogin());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = optionalWeatherUser.map(WeatherUser::getPassword)
                .orElse("$2a$10$Nx7Ww5Wb0bX.S2P4O7mQO.wYw8yVp2Y1G2Z2e2f2g2h2i2j2k2l2m");
        boolean matches = encoder.matches(userDto.getPassword(), password);

        if (optionalWeatherUser.isPresent() && matches) {
            long id = optionalWeatherUser.get().getId();
            UUID uuid = UUID.randomUUID();
            Cookie cookie = new Cookie("uuid", uuid.toString());
            cookie.setMaxAge(3600);
            cookie.setPath("/");
            cookie.setHttpOnly(true);

            Instant expiresAt = Instant.now().plusSeconds(3600);
            sessionDao.addSession(new WeatherSession(uuid, id), expiresAt);
            response.addCookie(cookie);

            return "redirect:/users/index";
        }
        bindingResult.reject("Auth error", "Login or password is incorrect");
        return "sign-in-with-errors";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto) {
        return "sign-up";
    }

    @GetMapping("/sign-up")
    public String signUp(@ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto) {
        return "sign-up";
    }

    @PostMapping("/sign-out")
    public String signUp(@CookieValue(value = "uuid", required = false) String token, HttpServletResponse response) {
        if (token == null) {
            return "redirect:/users/sign-in";
        }
        try {
            UUID uuid = UUID.fromString(token);
            sessionDao.deleteSession(uuid);
        } catch (IllegalArgumentException ignored) {
        }

        Cookie cookie = new Cookie("uuid", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/users/sign-in";
    }

    @GetMapping("/index")
    public String index(@CookieValue(value = "uuid", required = false) String token, Model model) {
        if (token == null) {
            return "redirect:/users/sign-in";
        }
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            return "redirect:/users/sign-in";
        }
        Optional<Long> userIdOptional = sessionDao.getUserIdAndRefreshSession(expiresAt, uuid, now);
        if (userIdOptional.isPresent()) {
            long userId = userIdOptional.get();
            Optional<WeatherUser> userOptional = userDao.findById(userId);
            if (userOptional.isPresent()) {
                List<WeatherLocation> locations = locationDao.getLocationsByUserId(userId);
                model.addAttribute("weatherUser", userOptional.get());
            }
        }
        return "index";
    }

    @GetMapping("/search-results")
    public String searchResults(@CookieValue(value = "uuid", required = false) String token,
                                @RequestParam ("location") String location, Model model) {
        if (token == null) {
            return "redirect:/users/sign-in";
        }
        model.addAttribute("weather", apiService.getWeather(location));
        //TODO add attribute WeatherUser
        Instant expiresAt = Instant.now().plusSeconds(3600);
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            return "redirect:/users/sign-in";
        }

        return "search-results";
    }
}
