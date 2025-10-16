package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.user.account.token.enums.UserAccountTokenType;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "user_account_tokens")
public class UserAccountToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount account;

    @Column
    private String token;

    @Column
    @Enumerated(EnumType.STRING)
    private UserAccountTokenType type;

    @Column
    private boolean used;

    @Column
    private Instant createdAt;

    @Column
    private Instant expiresAt;

    public UserAccountToken() {
        this.used = false;
        this.token = generateToken();
        this.type = UserAccountTokenType.ACCOUNT_VERIFICATION;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
    }

    public UserAccountToken(UserAccount account) {
        this();
        this.account = account;
    }

    public static UserAccountToken create() {
        return new UserAccountToken();
    }

    public UserAccount getAccount() {
        return this.account;
    }

    public UserAccountToken setAccount(UserAccount account) {
        this.account = account;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserAccountToken setId(Long id) {
        this.id = id;
        return this;
    }

    //    public Long getUserId() {
    //        return this.user.getId();
    //    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserAccountToken setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UserAccountToken setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public boolean isUsed() {
        return used;
    }

    public UserAccountToken setUsed(boolean used) {
        this.used = used;
        return this;
    }

    public UserAccountTokenType getType() {
        return type;
    }

    public UserAccountToken setType(UserAccountTokenType type) {
        this.type = type;
        return this;
    }

    public String getToken() {
        return token;
    }

    public UserAccountToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
