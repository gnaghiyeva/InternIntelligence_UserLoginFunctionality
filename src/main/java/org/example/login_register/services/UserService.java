package org.example.login_register.services;

import org.example.login_register.dtos.RegisterDto;

public interface UserService {
    boolean register(RegisterDto registerDto);
}
