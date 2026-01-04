package com.damian.xBank.modules.user.token.domain.model;

import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "user_account_tokens")
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
        return UUID.randomUUID().toString();
    }
}
