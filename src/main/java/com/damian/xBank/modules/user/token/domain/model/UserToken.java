package com.damian.xBank.modules.user.token.domain.model;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "user_tokens")
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column
    private String token;

    @Column
    @Enumerated(EnumType.STRING)
    private UserTokenType type;

    @Column
    private boolean used;

    @Column
    private Instant createdAt;

    @Column
    private Instant expiresAt;

    public UserToken() {
        this.used = false;
        this.token = generateToken();
        this.type = UserTokenType.ACCOUNT_VERIFICATION;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
    }

    public UserToken(User user) {
        this();
        this.user = user;
    }

    public static UserToken create() {
        return new UserToken();
    }

    public User getUser() {
        return this.user;
    }

    public UserToken setUser(User user) {
        this.user = user;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserToken setId(Long id) {
        this.id = id;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserToken setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UserToken setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public boolean isUsed() {
        return used;
    }

    public UserToken setUsed(boolean used) {
        this.used = used;
        return this;
    }

    public UserTokenType getType() {
        return type;
    }

    public UserToken setType(UserTokenType type) {
        this.type = type;
        return this;
    }

    public String getToken() {
        return token;
    }

    public UserToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String generateToken() {
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
        return UUID.randomUUID().toString();
    }

    public void generateVerificationToken() {
        this.type = UserTokenType.ACCOUNT_VERIFICATION;
        this.token = generateToken();
    }

    public void generateResetPasswordToken() {
        this.type = UserTokenType.RESET_PASSWORD;
        this.token = generateToken();
    }

    public void assertNotUsed() {
        // check if token is already used
        if (this.isUsed()) {
            throw new UserTokenUsedException(this.getUser().getId(), token);
        }
    }

    public void assertNotExpired() {
        // check expiration
        if (!this.getExpiresAt().isAfter(Instant.now())) {
            throw new UserTokenExpiredException(this.getUser().getId(), token);
        }
    }

    public void markAsUsed() {
        this.used = true;
    }
}
