package com.zitraksmoode.crypto.forge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "portfolios")
@Data @NoArgsConstructor @AllArgsConstructor
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)  // orphanRemoval = delete removed holdings
    private List<Holding> holdings = new ArrayList<>();

    public void addHolding(Holding holding) {
        holdings.add(holding);
        holding.setPortfolio(this);
    }
    @Transient
    public BigDecimal getTotalValue() {
        return holdings.stream()
                .map(h -> h.getQuantity().multiply(h.getBuyPrice()))  // Пока buyPrice, later currentPrice
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}