package com.damian.xBank.modules.payment.intent.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_intents")
public class PaymentIntent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id", nullable = false)
    private User merchant;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private BankingAccountCurrency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;

    private String merchantCallbackUrl;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public PaymentIntent() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = PaymentIntentStatus.PENDING;
    }

    public PaymentIntent(
            final User merchant,
            final BigDecimal amount,
            final BankingAccountCurrency currency
    ) {
        this();
        this.merchant = merchant;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getMerchantName() {
        return merchant.getProfile().getFirstName();
    }

    public User getMerchant() {
        return merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingAccountCurrency getCurrency() {
        return currency;
    }

    public PaymentIntentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void authorize() {
        this.status = PaymentIntentStatus.AUTHORIZED;
        this.updatedAt = Instant.now();
    }

    public void capture() {
        this.status = PaymentIntentStatus.CAPTURED;
        this.updatedAt = Instant.now();
    }

    public void assertPending() {
        if (this.status != PaymentIntentStatus.PENDING) {
            throw new PaymentIntentNotPendingException(this.id);
        }
    }

    @Override
    public String toString() {
        return "Payment{"
               + "id=" + id
               + ", user=" + merchant.toString()
               + ", amount=" + amount
               + ", currency=" + currency
               + ", status=" + status
               + ", createdAt=" + createdAt
               + ", updatedAt=" + updatedAt
               + "}";
    }

    public String getMerchantCallbackUrl() {
        return merchantCallbackUrl;
    }

    public void setMerchantCallbackUrl(String merchantCallbackUrl) {
        this.merchantCallbackUrl = merchantCallbackUrl;
    }
}
