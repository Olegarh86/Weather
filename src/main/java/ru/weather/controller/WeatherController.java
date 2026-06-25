package ru.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {

    @GetMapping
    public String weather(){
        return "redirect:/users";
    }

    @GetMapping("/search-results")
    public String searchResult(){
        return "search-results";
    }
}
