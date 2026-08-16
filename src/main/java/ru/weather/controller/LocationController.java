package ru.weather.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.weather.dto.UserLocationsDto;
import ru.weather.exception.LocationAlreadyExist;
import ru.weather.service.LocationService;

@Controller
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationServiceImpl;

    @Autowired
    public LocationController(LocationService locationServiceImpl) {
        this.locationServiceImpl = locationServiceImpl;
    }

    @PostMapping("/add")
    public String addLocation(UserLocationsDto location) {
        try {
            locationServiceImpl.saveNewLocation(location);
        } catch (LocationAlreadyExist e) {
            return "redirect:/index";
        }
        return "redirect:/index";
    }

    @PostMapping("/delete")
    public String deleteLocation(@RequestParam("latitude") String latitude,
                                 @RequestParam("longitude") String longitude, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        locationServiceImpl.deleteLocation(userId, latitude, longitude);
        return "redirect:/index";
    }
}
