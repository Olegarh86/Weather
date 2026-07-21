package ru.weather.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.weather.dto.UserSignUpDto;

@Service
public class PasswordEncoderServiceImpl implements PasswordEncoderService {
    private final BCryptPasswordEncoder encoder;

    public PasswordEncoderServiceImpl() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encodePassword(UserSignUpDto userSignUpDto) {
        return encoder.encode(userSignUpDto.getPassword());
    }

    @Override
    public boolean matches(String password, String password1) {
        return encoder.matches(password, password1);
    }
}