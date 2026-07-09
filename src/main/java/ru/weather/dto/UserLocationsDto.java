package ru.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocationsDto {
    private String name;
    @JsonProperty("user_id")
    private String userId;
    private String latitude;
    private String longitude;
}
