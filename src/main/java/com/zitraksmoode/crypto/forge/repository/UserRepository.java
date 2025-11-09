package com.zitraksmoode.crypto.forge.repository;

import com.zitraksmoode.crypto.forge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUsersByEmailAndPassword(String email, String password);

    Optional<User> findByEmail(String email);
}
