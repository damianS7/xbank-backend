package com.damian.xBank.modules.payment.intent.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotPendingException;
import com.damian.xBank.modules.user.merchant.domain.Merchant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    private Merchant merchant;

    @Column
    private String orderId;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private BankingAccountCurrency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;

    @Column
    private String description;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    protected PaymentIntent() {
    }

    PaymentIntent(
        final Merchant merchant,
        final BigDecimal amount,
        final BankingAccountCurrency currency,
        final String orderId,
        final String description
    ) {
        this.merchant = merchant;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = PaymentIntentStatus.PENDING;
    }

    public static PaymentIntent create(
        final Merchant merchant,
        final String orderId,
        final BigDecimal amount,
        final BankingAccountCurrency currency,
        final String description
    ) {
        return new PaymentIntent(
            merchant,
            amount,
            currency,
            orderId,
            description
        );
    }

    public Long getId() {
        return id;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Merchant getMerchant() {
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

    public String getDescription() {
        return description;
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
        return "PaymentIntent{"
               + "id=" + id
               + ", merchant=" + merchant.getId()
               + ", amount=" + amount
               + ", currency=" + currency
               + ", status=" + status
               + ", createdAt=" + createdAt
               + ", updatedAt=" + updatedAt
               + "}";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
