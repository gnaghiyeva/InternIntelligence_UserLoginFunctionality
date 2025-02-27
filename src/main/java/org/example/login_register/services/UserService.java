package org.example.login_register.services;

import org.example.login_register.dtos.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    boolean register(RegisterDto registerDto);

    String verifyEmail(String token);

    boolean login(LoginDto loginDto);

    boolean forgetPassword(ForgetPasswordDto forgetPasswordDto);

    boolean resetPassword(ResetPasswordDto resetPasswordDto);

    List<UserDto> getAllUsers();


}
