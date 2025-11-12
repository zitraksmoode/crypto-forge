package com.zitraksmoode.crypto.forge.service;

import com.zitraksmoode.crypto.forge.entity.Holding;
import com.zitraksmoode.crypto.forge.entity.Portfolio;
import com.zitraksmoode.crypto.forge.entity.User;
import com.zitraksmoode.crypto.forge.repository.UserRepository;
import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repo;
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
    /**
     * Регистрация нового пользователя с созданием портфеля и initial holdings.
     * Метод выполняется в транзакции: либо сохраняет User + Portfolio + Holdings, либо откатывает всё.
     * Валидация входных данных, проверка на дубликат, инициализация holdings с USDT и BTC.
     */
    @Transactional
    public User register(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is Empty");
        }
        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            throw new IllegalArgumentException("Password is Empty or too short");
        }

        Optional<User> existing = repo.findByEmail(email);
        if (existing.isPresent()) {
            log.warn("Duplicate registration attempt for email: {}", email);
            throw new IllegalArgumentException("User with email -> " + email + " already exists");
        }
        log.info("Starting registration for email: {}", email);
        User user = User.builder()
                .email(email)
                .password(password) // TODO: Хэширование пароля с BCrypt для безопасности (добавить Spring Security)
                .build();

        Portfolio pf = new Portfolio();
        pf.setUser(user);
        Holding usdtHolding = Holding.builder()
                .portfolio(pf) // Sync-setter добавит в pf.getHoldings()
                .asset("USDT")
                .quantity(BigDecimal.valueOf(1000))
                .buyPrice(BigDecimal.ONE) // USDT = 1 USD
                .build();

        Holding btcHolding = Holding.builder()
                .portfolio(pf)
                .asset("BTC")
                .quantity(BigDecimal.valueOf(0.1))
                .buyPrice(BigDecimal.valueOf(60000))
                .build();
        User savedUser = repo.save(user);
        log.info("User registered successfully: ID={}, email={}, holdings: USDT={}, BTC={}",
                savedUser.getId(), email, usdtHolding.getQuantity(), btcHolding.getQuantity());

        return savedUser;
    }
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId, String asset) {
        Optional<User> opt = repo.findById(userId);
        if (opt.isEmpty()) return BigDecimal.ZERO;
        return opt.map(u -> Optional.ofNullable(u.getPortfolio())
                .map(Portfolio::getHoldings)
                .orElse(Collections.emptyList())
                .stream()
                .filter(h -> h.getAsset().equals(asset))
                .findFirst()
                .map(Holding::getQuantity)
                .orElse(BigDecimal.ZERO)).orElse(BigDecimal.ZERO);  // Внешний optional, если нужно
    }
    @Transactional(readOnly = true)
    public BigDecimal totalBalance(Long userId){
        Optional<User> optUser = repo.findById(userId);
        if (optUser.isEmpty()) return BigDecimal.ZERO;
        return optUser.map(User::getPortfolio).
                map(Portfolio::getTotalValue).
                orElse(BigDecimal.ZERO);
    }
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Async
    public CompletableFuture<Void> updateHoldingQuantity(Long userId, String asset, BigDecimal delta) {

        if (userId == null)  throw new IllegalArgumentException("User not found...");
        if (asset == null || asset.trim().isEmpty())  throw new IllegalArgumentException("Asset is Empty!");
        if (delta == null ) throw new IllegalArgumentException("Delta is Empty!");
        Optional<User> optUser = repo.findById(userId);
        if(optUser.isEmpty()){
            log.warn("This user is not created");
            throw new IllegalArgumentException("User with id -> " + userId + " not exists");
        }
        log.info("Starting updating for id: {}", userId);
        Optional<Holding> optHolding = optUser
                .map(User::getPortfolio)
                .map(Portfolio::getHoldings)
                .orElse(Collections.emptyList())
                .stream()
                .filter(h -> h.getAsset().equals(asset))
                .findFirst();
        if(optHolding.isEmpty())
            throw new IllegalArgumentException("This user dont have holding quantity");
        Holding holding = optHolding.get();
        BigDecimal newQuantity = holding.getQuantity().add(delta);
        holding.setQuantity(newQuantity);
        repo.save(optUser.get());
        log.info("Updated {} quantity to {} for user {}", asset, newQuantity, userId);
        return CompletableFuture.completedFuture(null);
    }
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return repo.findByEmail(email);
    }
    @Transactional
    public void deleteUser(Long userId){
        Optional<User> user = repo.findById(userId);
        if(user.isEmpty()) throw new IllegalArgumentException("Такого пользователя не существует!");
        repo.delete(user.get());
        log.info("Deleted user {}", userId);
    }

}