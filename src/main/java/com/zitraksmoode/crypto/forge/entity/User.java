package com.zitraksmoode.crypto.forge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Transient
    private String jwtToken;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private Portfolio portfolio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now(); }
    @PrePersist
    private void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }
}