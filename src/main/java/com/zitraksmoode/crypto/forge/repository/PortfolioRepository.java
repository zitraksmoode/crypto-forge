package com.zitraksmoode.crypto.forge.repository;

import com.zitraksmoode.crypto.forge.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
