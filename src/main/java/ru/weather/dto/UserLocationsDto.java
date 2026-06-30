package ru.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocationsDto {
    private String name;
    private String user_id;
    private String latitude;
    private String longitude;
}
