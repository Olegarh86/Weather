package ru.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.weather.annotation.CurrentUser;
import ru.weather.dto.CardLocationDto;
import ru.weather.dto.CurrentUserDto;
import ru.weather.dto.ResponseWithCoordinates;
import ru.weather.model.Location;
import ru.weather.service.LocationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class WeatherController {
    private final LocationService locationServiceImpl;

    @Autowired
    public WeatherController(LocationService locationServiceImpl) {
        this.locationServiceImpl = locationServiceImpl;
    }

    @GetMapping("/index")
    public CompletableFuture<String> index(@CurrentUser CurrentUserDto user,
                                           Model model) {
        List<Location> locations = locationServiceImpl.getLocations(user);
        CompletableFuture<List<CardLocationDto>> cardLocations = locationServiceImpl.getAllWeathers(user, locations);
        model.addAttribute("allLocations", cardLocations);
        return cardLocations.thenApply(allLocations -> {
            model.addAttribute("allLocations", allLocations);
            return "index";
        });
    }

    @GetMapping("/search-results")
    public String searchResults(@RequestParam("location") String locationName,
                                Model model,
                                @CurrentUser CurrentUserDto user) {
        ResponseWithCoordinates[] allLocations = locationServiceImpl.findAllLocationsByName(locationName);
        model.addAttribute("id", user.getUserId());
        model.addAttribute("locationName", locationName);
        model.addAttribute("allLocations", allLocations);
        return "search-results";
    }
}
