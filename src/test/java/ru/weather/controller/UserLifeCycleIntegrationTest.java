package ru.weather.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.weather.config.WeatherConfig;
import ru.weather.dao.SessionDao;
import ru.weather.dto.UserDto;
import ru.weather.exception.SessionNotFound;
import ru.weather.exception.UserNotFoundException;
import ru.weather.service.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WeatherConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class UserLifeCycleIntegrationTest {
    private static final String WEATHER_USERS_PATH = "/weather/users";
    private static final String SIGN_IN_PATH = "/weather/users/sign-in";
    private static final String INDEX_PATH = "/weather/users/index";
    private static final String LOGIN_PATH = "/weather/users/login";
    private static final String SIGN_UP_WITH_ERRORS = "sign-up-with-errors";
    private static final String SIGN_IN_WITH_ERRORS = "sign-in-with-errors";
    private static final String LOGIN_KEY = "login";
    private static final String LOGIN_VALUE = "test";
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD_VALUE = "test1234";
    private static final String SHORT_PASSWORD_VALUE = "test123";
    private static final String PASSWORD_CONFIRM_KEY = "passwordConfirm";
    private static final String REDIRECT_TO_KEY = "redirect_to";
    private static final String COOKIE_KEY = "uuid";
    private static final String REDIRECT = "redirect:";
    private static final String USER_SIGN_UP_DTO = "userSignUpDto";
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SessionCleanupService sessionCleanupService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void successfulSignUpTest() throws Exception {
        mockMvc.perform(post(WEATHER_USERS_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(PASSWORD_CONFIRM_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, SIGN_IN_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + SIGN_IN_PATH));

        UserDto testUser = new UserDto();
        testUser.setLogin(LOGIN_VALUE);

        Long userId = userProfileService.getUserId(testUser);
        String login = userProfileService.getLogin(userId);
        assertNotNull(userId);
        assertEquals(LOGIN_VALUE, login);
    }

    @Test
    public void passwordNotConfirmCreateUserTest() throws Exception {
        mockMvc.perform(post(WEATHER_USERS_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(PASSWORD_CONFIRM_KEY, "11111")
                        .param(REDIRECT_TO_KEY, SIGN_IN_PATH))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(SIGN_UP_WITH_ERRORS))
                .andExpect(model().attributeHasFieldErrorCode(
                        USER_SIGN_UP_DTO, PASSWORD_CONFIRM_KEY, "NotConfirm.weatherUser.password"));

        UserDto testUser = new UserDto();
        testUser.setLogin(LOGIN_VALUE);

        assertThrows(UserNotFoundException.class, () -> userProfileService.getUserId(testUser));
    }

    @Test
    public void passwordIncorrectSignInTest() throws Exception {
        successfulSignUpTest();
        mockMvc.perform(post(LOGIN_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, "test12345")
                        .param(REDIRECT_TO_KEY, INDEX_PATH))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(SIGN_IN_WITH_ERRORS))
                .andExpect(model().attributeHasFieldErrorCode(
                        "userSignInDto", PASSWORD_KEY, "Incorrect.weatherUser.password"));
    }

    @Test
    public void passwordTooShortSignUpTest() throws Exception {
        mockMvc.perform(post(WEATHER_USERS_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, SHORT_PASSWORD_VALUE)
                        .param(PASSWORD_CONFIRM_KEY, SHORT_PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, SIGN_IN_PATH))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(SIGN_UP_WITH_ERRORS))
                .andExpect(model().attributeHasFieldErrorCode(
                        USER_SIGN_UP_DTO, PASSWORD_KEY, "Size.weatherUser.password"
                ));

        UserDto testUser = new UserDto();
        testUser.setLogin(LOGIN_VALUE);

        assertThrows(UserNotFoundException.class, () -> userProfileService.getUserId(testUser));
    }

    @Test
    public void loginAlreadyExistTest() throws Exception {
        successfulSignUpTest();
        mockMvc.perform(post(WEATHER_USERS_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(PASSWORD_CONFIRM_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, SIGN_IN_PATH))
                .andExpect(status().is2xxSuccessful())
        .andExpect(view().name(SIGN_UP_WITH_ERRORS))
        .andExpect(model().attributeHasFieldErrorCode(USER_SIGN_UP_DTO, LOGIN_KEY, "AlreadyExist.weatherUser.login"));
    }

    @Test
    public void successfulSignInTest() throws Exception {
        successfulSignUpTest();
        MvcResult result = mockMvc.perform(post(LOGIN_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, INDEX_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + INDEX_PATH)).andReturn();

        String token = Objects.requireNonNull(result.getResponse().getCookie(COOKIE_KEY)).getValue();
        UUID uuid = UUID.fromString(token);
        Long id = sessionService.getSession(uuid);
        assertNotNull(id);
    }

    @Test
    public void createAndCleanUpSessionTest() throws Exception {
        successfulSignUpTest();
        MvcResult result = mockMvc.perform(post(LOGIN_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, INDEX_PATH)).andReturn();

        String token = Objects.requireNonNull(result.getResponse().getCookie(COOKIE_KEY)).getValue();
        UUID uuid = UUID.fromString(token);
        Long id = sessionService.getSession(uuid);
        assertNotNull(id);

        Instant now = Instant.now();
        sessionDao.getUserIdAndRefreshSession(now.minusSeconds(1), uuid, now);

        Thread.sleep(100);
        sessionCleanupService.cleanup();

        assertThrows(SessionNotFound.class, () -> sessionService.getSession(uuid));
    }
}
