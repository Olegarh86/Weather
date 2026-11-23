package ru.weather.dto;

import lombok.Getter;

@Getter
public class CurrentUserDto {
    private final Long userId;
    private final String login;

    public CurrentUserDto(Long userId, String login) {
        this.userId = userId;
        this.login = login;
    }
}
