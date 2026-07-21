package ru.weather.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.weather.dto.UserSignUpDto;
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
    private static final String WEATHER_USERS_PATH = "/users";
    private static final String SIGN_IN_PATH = "/users/sign-in";
    private static final String INDEX_PATH = "/users/index";
    private static final String LOGIN_PATH = "/users/login";
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
    private SessionService sessionServiceImpl;
    @Autowired
    private UserProfileService userProfileServiceImpl;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SessionDao sessionDaoImpl;
    @Autowired
    private SessionCleanupService sessionCleanupServiceImpl;
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
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void passwordNotConfirmCreateUserTest() throws Exception {
        mockMvc.perform(post(WEATHER_USERS_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(PASSWORD_CONFIRM_KEY, "11111")
                        .param(REDIRECT_TO_KEY, SIGN_IN_PATH))
                .andExpect(status().is2xxSuccessful());

        UserDto testUser = new UserDto();
        testUser.setLogin(LOGIN_VALUE);
        assertThrows(UserNotFoundException.class, () -> userProfileServiceImpl.getUserId(testUser));
    }

    @Test
    public void passwordIncorrectSignInTest() throws Exception {
        registerTestUser();

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

        assertThrows(UserNotFoundException.class, () -> userProfileServiceImpl.getUserId(testUser));
    }

    @Test
    public void loginAlreadyExistTest() throws Exception {
        registerTestUser();
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
        registerTestUser();
        MvcResult result = mockMvc.perform(post(LOGIN_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, INDEX_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + INDEX_PATH)).andReturn();

        String token = Objects.requireNonNull(result.getResponse().getCookie(COOKIE_KEY)).getValue();
        UUID uuid = UUID.fromString(token);
        Long id = sessionServiceImpl.getSession(uuid);
        assertNotNull(id);
    }

    @Test
    public void createAndCleanUpSessionTest() throws Exception {
        registerTestUser();
        MvcResult result = mockMvc.perform(post(LOGIN_PATH)
                        .param(LOGIN_KEY, LOGIN_VALUE)
                        .param(PASSWORD_KEY, PASSWORD_VALUE)
                        .param(REDIRECT_TO_KEY, INDEX_PATH)).andReturn();

        String token = Objects.requireNonNull(result.getResponse().getCookie(COOKIE_KEY)).getValue();
        UUID uuid = UUID.fromString(token);
        Long id = sessionServiceImpl.getSession(uuid);
        assertNotNull(id);

        Instant now = Instant.now();
        sessionDaoImpl.getUserIdAndRefreshSession(now.minusSeconds(1), uuid, now);

        Thread.sleep(100);
        sessionCleanupServiceImpl.cleanup();

        assertThrows(SessionNotFound.class, () -> sessionServiceImpl.getSession(uuid));
    }

    private void registerTestUser() {
        UserSignUpDto signUpDto = new UserSignUpDto();
        signUpDto.setLogin(LOGIN_VALUE);
        signUpDto.setPassword(PASSWORD_VALUE);
        signUpDto.setPasswordConfirm(PASSWORD_VALUE);

        userProfileServiceImpl.createNewUser(signUpDto);
    }
}
