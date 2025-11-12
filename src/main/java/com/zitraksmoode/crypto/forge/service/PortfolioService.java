package com.zitraksmoode.crypto.forge.service;

import com.zitraksmoode.crypto.forge.repository.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PortfolioService {
    private static final Logger log = LoggerFactory.getLogger(PortfolioService.class);
    private final PortfolioRepository repo;
    public PortfolioService(PortfolioRepository repo) {
        this.repo = repo;
    }


}
