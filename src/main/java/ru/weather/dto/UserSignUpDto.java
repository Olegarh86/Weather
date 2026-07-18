package ru.weather.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignUpDto {
    @NotEmpty(message = "{NotEmpty.weatherUser.login}")
    @Size(min = 2, max = 20, message = "{Size.weatherUser.login}")
    private String login;
    @NotEmpty(message = "{NotEmpty.weatherUser.password}")
    @Size(min = 8, max = 64, message = "{Size.weatherUser.password}")
    private String password;
    private String passwordConfirm;

    public UserSignUpDto() {
    }
}
