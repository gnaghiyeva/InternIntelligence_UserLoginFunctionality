package org.example.login_register.dtos;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String verificationCode;
    private String newPassword;
}
