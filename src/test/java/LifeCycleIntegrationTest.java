import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.weather.config.WeatherConfig;
import ru.weather.dto.UserDto;
import ru.weather.service.PasswordEncoderService;
import ru.weather.service.SessionService;
import ru.weather.service.UserProfileService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WeatherConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class LifeCycleIntegrationTest {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private PasswordEncoderService passwordEncoderService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }


    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(post("/weather/users")
                .param("login", "test")
                .param("password", "test")
                .param("passwordConfirm", "test")
                .param("redirect_to", "/weather/users/sign-in")).andExpect(status().is3xxRedirection());

        UserDto testUser = new UserDto();
        testUser.setLogin("test");

        Long userId = userProfileService.getUserId(testUser);
        String login = userProfileService.getLogin(userId);
        assertNotNull(userId);
        assertEquals("test", login);
    }
}
