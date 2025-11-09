package com.zitraksmoode.crypto.forge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "holdings", indexes = {
        @Index(columnList = "asset"),
        @Index(columnList = "buyDate"),
        @Index(columnList = "portfolio_id")
})@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Связь с портфелем (owning side, FK portfolio_id)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false)
    @NotBlank(message = "Asset name required")  // Добавь для asset
    private String asset;

    @Column(nullable = false, precision = 38, scale = 8)
    @Positive(message = "Quantity must be positive")  // Нет negative holdings
    private BigDecimal quantity;

    @Column(nullable = false)
    @PastOrPresent(message = "Buy date cannot be in the future")  // Логично для истории
    private LocalDateTime buyDate;

    @Column(nullable = false, precision = 38, scale = 2)
    @Positive(message = "Buy price must be positive")
    private BigDecimal buyPrice;

    public void setPortfolio(Portfolio portfolio) {
        if (this.portfolio != null) {
            this.portfolio.getHoldings().remove(this);  // Удаляем из старого, если был
        }
        this.portfolio = portfolio;
        if (portfolio != null && !portfolio.getHoldings().contains(this)) {
            portfolio.getHoldings().add(this);  // Добавляем в новый
        }
    }
    @PrePersist
    private void prePersist() {
        if (buyDate == null) buyDate = LocalDateTime.now();
    }
}