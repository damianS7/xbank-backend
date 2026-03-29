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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Merchant create(
        String merchantName,
        String callbackUrl
    ) {
        return new Merchant(null, null, merchantName, callbackUrl, Instant.now());
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
