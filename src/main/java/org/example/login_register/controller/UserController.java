package org.example.login_register.controller;

import org.example.login_register.dtos.LoginDto;
import org.example.login_register.dtos.RegisterDto;
import org.example.login_register.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
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
}
