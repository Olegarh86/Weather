package ru.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.weather.annotation.CurrentUser;
import ru.weather.dto.CurrentUserDto;
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
    public String addLocation(@CurrentUser CurrentUserDto user,
                              UserLocationsDto location, RedirectAttributes redirectAttributes) {
        try {
            locationServiceImpl.saveNewLocation(location, user);

        } catch (LocationAlreadyExist ex) {
            redirectAttributes.addFlashAttribute("alreadyExistError",
                    "That location already exist in your list");
        }
        return "redirect:/index";
    }

    @PostMapping("/delete")
    public String deleteLocation(@RequestParam("latitude") String latitude,
                                 @RequestParam("longitude") String longitude,
                                 @CurrentUser CurrentUserDto user) {
        locationServiceImpl.deleteLocation(user, latitude, longitude);
        return "redirect:/index";
    }
}
