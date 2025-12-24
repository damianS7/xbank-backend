package com.damian.xBank.modules.banking.transfer.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.transfer.domain.enums.BankingTransferStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "banking_transfers")
public class BankingTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id")
    private BankingAccount fromAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_account_id")
    private BankingAccount toAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankingTransferStatus status;

    @Column(length = 255, nullable = false)
    private String description;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingTransfer() {
        this.status = BankingTransferStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public static BankingTransfer create() {
        return new BankingTransfer();
    }

    public Long getId() {
        return id;
    }

    public BankingTransfer setId(Long id) {
        this.id = id;
        return this;
    }

    public BankingTransferStatus getStatus() {
        return status;
    }

    public BankingTransfer setStatus(BankingTransferStatus status) {
        this.status = status;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BankingTransfer setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BankingTransfer setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BankingAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(BankingAccount toAccount) {
        this.toAccount = toAccount;
    }

    public BankingAccount getFromAccount() {
        return fromAccount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFromAccount(BankingAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void confirm() {
        this.status = BankingTransferStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void reject() {
        this.status = BankingTransferStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

}
