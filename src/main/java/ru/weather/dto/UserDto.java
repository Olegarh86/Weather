package ru.weather.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private long id;
    @NotEmpty(message = "Login should not be empty")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    private String login;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, max = 64, message = "Password should be between 8 and 64 characters")
    private String password;
    private String location;

    public UserDto() {
    }

    public UserDto(long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
}