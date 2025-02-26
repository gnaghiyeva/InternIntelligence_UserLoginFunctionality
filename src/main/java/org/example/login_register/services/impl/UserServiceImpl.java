package org.example.login_register.services.impl;

import org.example.login_register.dtos.LoginDto;
import org.example.login_register.dtos.RegisterDto;
import org.example.login_register.models.User;
import org.example.login_register.repositories.UserRepository;
import org.example.login_register.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        User existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            return false;
        }
        if (!isValidPassword(registerDto.getPassword())) {
            throw new IllegalArgumentException("The password must be at least 8 characters long and contain one uppercase letter, one lowercase letter, one number, and one special character.");
        }
        User newUser = modelMapper.map(registerDto, User.class);
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null) {
            return false;
        }

        if (bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
