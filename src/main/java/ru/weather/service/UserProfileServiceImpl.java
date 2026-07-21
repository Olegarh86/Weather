package ru.weather.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.weather.dao.UserDao;
import ru.weather.dao.UserDaoImpl;
import ru.weather.dto.UserDto;
import ru.weather.dto.UserSignUpDto;
import ru.weather.exception.LoginAlreadyExist;
import ru.weather.exception.UserNotFoundException;
import ru.weather.model.WeatherUser;

import java.util.Optional;

@Service
public class UserProfileService {
    private final UserDao userDaoImpl;

    @Autowired
    public UserProfileService(UserDao userDaoImpl) {
        this.userDaoImpl = userDaoImpl;
    }

    @Transactional
    public void createNewUser(@Valid UserSignUpDto userSignUpDto) {
        WeatherUser weatherUser = new WeatherUser(userSignUpDto.getLogin(), userSignUpDto.getPassword());
        try {
            userDaoImpl.saveUser(weatherUser);
        } catch (DataAccessException e) {
            throw new LoginAlreadyExist(e);
        }
    }

    public String getPassword(UserDto userDto) {
        Optional<WeatherUser> optionalWeatherUser = userDaoImpl.findByLogin(userDto.getLogin());
        if (optionalWeatherUser.isPresent()) {
            return optionalWeatherUser.get().getPassword();
        }
        throw new UserNotFoundException();
    }

    public Long getUserId(UserDto userDto) {
        Optional<WeatherUser> optionalWeatherUser = userDaoImpl.findByLogin(userDto.getLogin());
        if (optionalWeatherUser.isPresent()) {
            return optionalWeatherUser.get().getId();
        }
        throw new UserNotFoundException();
    }

    public String getLogin(Long userId) {
        Optional<WeatherUser> userOptional = userDaoImpl.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userOptional.get().getLogin();
    }
}
