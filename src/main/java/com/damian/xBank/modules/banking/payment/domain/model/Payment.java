package com.damian.xBank.modules.banking.payment.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "banking_payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long invoiceId;

    @Column
    private String merchant;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private BankingAccountCurrency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BankingAccountCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(BankingAccountCurrency currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payment{"
               + "id=" + id
               + ", invoiceId=" + invoiceId
               + ", merchant='" + merchant + '\''
               + ", amount=" + amount
               + ", currency=" + currency
               + ", status=" + status
               + ", createdAt=" + createdAt
               + ", updatedAt=" + updatedAt
               + "}";
    }
}
