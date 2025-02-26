package org.example.login_register.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "name", nullable = false, length = 25)
    private String firstName;

    @Column(name = "surname", nullable = false, length = 25)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    private String verificationCode;
}