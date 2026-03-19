package com.damian.xBank.modules.user.token.domain.model;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

    // JPA constructor
    protected UserToken() {
    }

    UserToken(
        Long tokenId,
        User user,
        UserTokenType type,
        boolean used,
        Instant expiresAt
    ) {
        this.id = tokenId;
        this.user = user;
        this.used = used;
        this.token = generateToken();
        this.expiresAt = expiresAt != null ? expiresAt : Instant.now().plus(1, ChronoUnit.DAYS);
        this.type = type != null ? type : UserTokenType.ACCOUNT_VERIFICATION;
        this.createdAt = Instant.now();
    }

    public static UserToken create(User user, UserTokenType type) {
        return new UserToken(null, user, type, false, null);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void expiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public UserTokenType getType() {
        return type;
    }

    public String getToken() {
        return token;
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

    /**
     * Uses the token
     */

    public void markAsUsed() {
        this.used = true;
    }
}
