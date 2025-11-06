package com.zitraksmoode.crypto.forge.repository;

import com.zitraksmoode.crypto.forge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {
}
