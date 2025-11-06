package com.zitraksmoode.crypto.forge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email required")
    @Column(unique = true)
    private String email;

    @Size(min = 6, message = "Password too short")
    @NotBlank(message = "Password required")
    private String password;

    private String jwtToken;

    @OneToOne(mappedBy = "user")
    private Portfolio portfolio;
}