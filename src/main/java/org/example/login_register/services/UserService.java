package org.example.login_register.services;

import org.example.login_register.dtos.ForgetPasswordDto;
import org.example.login_register.dtos.LoginDto;
import org.example.login_register.dtos.RegisterDto;
import org.example.login_register.dtos.ResetPasswordDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    boolean register(RegisterDto registerDto);

    String verifyEmail(String token);

    boolean login(LoginDto loginDto);

    boolean forgetPassword(ForgetPasswordDto forgetPasswordDto);

    boolean resetPassword(ResetPasswordDto resetPasswordDto);


}
