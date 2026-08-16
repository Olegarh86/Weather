package ru.weather.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.weather.dto.CardLocationDto;
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
}
