package ru.weather.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import ru.weather.dto.CardLocationDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.exception.ConnectToWeatherServiceException;
import ru.weather.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {
    private final static String USER_ID_KEY = "userId";
    private final static String FIRST_COUNTRY = "First Country";
    private final static String SECOND_COUNTRY = "Second Country";
    private final static String FIRST_NAME = "First Name";
    private final static String SECOND_NAME = "Second Name";
    private final static String LONDON = "London";
    private final static String LONDON_LAT = "51.5085";
    private final static String LONDON_LON = "-0.1257";
    private final static String ALL_LOCATIONS = "allLocations";
    private final static Long USER_ID_VALUE = 100L;
    private Model model;
    private HttpServletRequest req;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
        req = new MockHttpServletRequest();
        req.setAttribute(USER_ID_KEY, USER_ID_VALUE);
    }

    @Test
    void indexSuccessTest() {
        List<CardLocationDto> cardLocationsExpected = getCardLocationDto();

        when(locationService.getAllWeathers(USER_ID_VALUE)).thenReturn(cardLocationsExpected);

        assertEquals("index", usersController.index(req, model));
        Object cardLocationDtoResponse = model.getAttribute(ALL_LOCATIONS);
        List<CardLocationDto> cardLocationDtoResult = null;
        if (cardLocationDtoResponse instanceof List<?>) {
            cardLocationDtoResult = (List<CardLocationDto>) model.getAttribute(ALL_LOCATIONS);
        }

        assertNotNull(cardLocationDtoResult);
        assertEquals(cardLocationDtoResult.size(), cardLocationsExpected.size());
        assertEquals(FIRST_COUNTRY, cardLocationDtoResult.get(0).getCountry());
        assertEquals(FIRST_NAME, cardLocationDtoResult.get(0).getName());
        assertEquals(SECOND_COUNTRY, cardLocationDtoResult.get(1).getCountry());
        assertEquals(SECOND_NAME, cardLocationDtoResult.get(1).getName());
    }

    private static List<CardLocationDto> getCardLocationDto() {
        CardLocationDto dto1 = new CardLocationDto();
        dto1.setCountry(FIRST_COUNTRY);
        dto1.setName(FIRST_NAME);

        CardLocationDto dto2 = new CardLocationDto();
        dto2.setCountry(SECOND_COUNTRY);
        dto2.setName(SECOND_NAME);

        List<CardLocationDto> result = new ArrayList<>();
        result.add(dto1);
        result.add(dto2);
        return result;
    }

    @Test
    void indexFailedTest() {
        when(locationService.getAllWeathers(USER_ID_VALUE)).thenThrow(new ConnectToWeatherServiceException(new Exception()));
        assertThrows(ConnectToWeatherServiceException.class, () -> usersController.index(req, model));
    }

    @Test
    void searchResultsTest() {
        ResponseWithCoordinates[] response =  new ResponseWithCoordinates[1];
        response[0] = new ResponseWithCoordinates();
        response[0].setName(LONDON);
        response[0].setLat(LONDON_LAT);
        response[0].setLon(LONDON_LON);

        when(locationService.findAllLocationsByName(LONDON)).thenReturn(response);

        assertEquals("search-results", usersController.searchResults(LONDON, model, req));
        ResponseWithCoordinates[] allLocations = (ResponseWithCoordinates[]) model.getAttribute(ALL_LOCATIONS);

        assertEquals(USER_ID_VALUE, model.getAttribute("id"));
        assertEquals(LONDON, model.getAttribute("locationName"));
        assertNotNull(allLocations);
        assertEquals(LONDON, allLocations[0].getName());
        assertEquals(LONDON_LAT, allLocations[0].getLat());
        assertEquals(LONDON_LON, allLocations[0].getLon());
    }
}