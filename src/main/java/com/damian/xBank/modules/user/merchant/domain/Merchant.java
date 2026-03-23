package com.damian.xBank.modules.user.merchant.domain;

import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column
    private String merchantName;

    @Column
    private String callbackUrl;

    @Column
    private Instant createdAt;

    // JPA constructor
    protected Merchant() {
    }

    Merchant(
        Long id,
        String merchantName,
        String callbackUrl
    ) {
        this.id = id;
        this.merchantName = merchantName;
        this.callbackUrl = callbackUrl;
        this.createdAt = Instant.now();
    }

    public static Merchant create(
        String merchantName,
        String callbackUrl
    ) {
        return new Merchant(null, merchantName, callbackUrl);
    }

    public Long getId() {
        return id;
    }


    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
