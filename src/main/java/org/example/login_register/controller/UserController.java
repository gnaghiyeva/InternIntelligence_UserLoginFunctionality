package org.example.login_register.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.login_register.dtos.*;
import org.example.login_register.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(@ModelAttribute RegisterDto registerDto) {
        try {
            boolean isRegistered = userService.register(registerDto);
            if (!isRegistered) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed. This email address already exists.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration completed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration: " + e.getMessage());
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> login(@ModelAttribute LoginDto loginDto) {
        boolean isAuthenticated = userService.login(loginDto);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed. Email or password is incorrect.");
        }
        return ResponseEntity.ok("Login completed successfully.");
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        return userService.verifyEmail(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Exit done");
    }
    @PostMapping(value = "/forget-password",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> forgetPassword(@ModelAttribute ForgetPasswordDto forgetPasswordDto) {
        boolean result = userService.forgetPassword(forgetPasswordDto);
        if (result) {
            return ResponseEntity.ok("Password reset email sent.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
    }

    @PostMapping(value = "/reset-password",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> resetPassword(@ModelAttribute ResetPasswordDto resetPasswordDto) {
        boolean isReset = userService.resetPassword(resetPasswordDto);

        if (isReset) {
            return ResponseEntity.ok("Password successfully reset.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The email or confirmation code is incorrect.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
