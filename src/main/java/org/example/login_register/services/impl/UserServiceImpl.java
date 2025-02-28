package org.example.login_register.services.impl;

import org.example.login_register.config.JwtUtil;
import org.example.login_register.config.EmailService;
import org.example.login_register.dtos.*;
import org.example.login_register.models.User;
import org.example.login_register.repositories.UserRepository;
import org.example.login_register.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

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
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one number and one special character.");
        }

        User newUser = modelMapper.map(registerDto, User.class);
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        newUser.setVerificationCode(JwtUtil.generateToken(registerDto.getEmail()));
        userRepository.save(newUser);

        String verificationLink = "http://localhost:8585/auth/verify?token=" + newUser.getVerificationCode();

        emailService.sendVerificationEmail(registerDto.getEmail(), verificationLink);

        return true;
    }

    @Override
    public String verifyEmail(String token) {
        if (token == null || token.isEmpty()) {
            return "Invalid authentication request. Missing token!";
        }

        String email = JwtUtil.extractEmail(token);
        if (email == null) {
            return "Invalid or expired authentication token.";
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found. Please register again.";
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(token)) {
            return "Verification failed. Token is not valid.";
        }

        user.setVerificationCode(null);
        userRepository.save(user);

        return "Email verified successfully! You can now login.";
    }

    @Override
    public boolean login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null || !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return false;
        }

        if (user.getVerificationCode() != null) {
            throw new IllegalArgumentException("Please verify your email first.");
        }
        return true;
    }

    @Override
    public boolean forgetPassword(ForgetPasswordDto forgetPasswordDto) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(forgetPasswordDto.getEmail()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String verificationCode = generateVerificationCode();
            sendVerificationEmail(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(resetPasswordDto.getEmail()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getVerificationCode() != null && user.getVerificationCode().equals(resetPasswordDto.getVerificationCode())) {
                user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getNewPassword()));
                user.setVerificationCode(null);

                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password reset code");
        message.setText("Your confirmation code to reset your password: " + verificationCode);
        mailSender.send(message);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}
