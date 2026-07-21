package ru.weather.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.weather.dao.UserDao;
import ru.weather.dto.UserDto;
import ru.weather.dto.UserSignUpDto;
import ru.weather.exception.LoginAlreadyExist;
import ru.weather.exception.UserNotFoundException;
import ru.weather.model.WeatherUser;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserDao userDaoImpl;
    private final PasswordEncoderService passwordEncoder;

    @Autowired
    public UserProfileServiceImpl(UserDao userDaoImpl, PasswordEncoderService passwordEncoder) {
        this.userDaoImpl = userDaoImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createNewUser(@Valid UserSignUpDto userSignUpDto) {
        String passwordEncoded = passwordEncoder.encodePassword(userSignUpDto);

        WeatherUser weatherUser = new WeatherUser(userSignUpDto.getLogin(), passwordEncoded);
        try {
            userDaoImpl.saveUser(weatherUser);
        } catch (DataAccessException e) {
            throw new LoginAlreadyExist(e);
        }
    }

    @Override
    public String getPassword(UserDto userDto) {
        Optional<WeatherUser> optionalWeatherUser = userDaoImpl.findByLogin(userDto.getLogin());
        if (optionalWeatherUser.isPresent()) {
            return optionalWeatherUser.get().getPassword();
        }
        throw new UserNotFoundException();
    }

    @Override
    public Long getUserId(UserDto userDto) {
        Optional<WeatherUser> optionalWeatherUser = userDaoImpl.findByLogin(userDto.getLogin());
        if (optionalWeatherUser.isPresent()) {
            return optionalWeatherUser.get().getId();
        }
        throw new UserNotFoundException();
    }

    @Override
    public String getLogin(Long userId) {
        Optional<WeatherUser> userOptional = userDaoImpl.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userOptional.get().getLogin();
    }
}
