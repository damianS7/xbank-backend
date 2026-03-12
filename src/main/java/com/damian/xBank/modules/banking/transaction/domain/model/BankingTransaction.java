package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotPendingStatusException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
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
import java.util.Objects;

@Entity
@Table(name = "banking_transactions")
public class BankingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private BankingAccount bankingAccount;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private BankingCard bankingCard;

    @ManyToOne
    @JoinColumn(name = "transfer_id", referencedColumnName = "id")
    private BankingTransfer transfer;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column
    private String description;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private BankingTransactionType type;

    @Enumerated(EnumType.STRING)
    private BankingTransactionStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    protected BankingTransaction() {
        this.amount = BigDecimal.valueOf(0);
        this.status = BankingTransactionStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    BankingTransaction(
        Long transactionId,
        BankingAccount bankingAccount,
        BankingCard bankingCard,
        BankingTransfer transfer,
        BigDecimal amount,
        String description,
        BankingTransactionType type,
        BankingTransactionStatus status
    ) {
        this();
        this.id = transactionId;
        this.bankingAccount = bankingAccount;
        this.bankingCard = bankingCard;
        this.transfer = transfer;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.calcBalanceBefore();
        this.calcBalanceAfter();
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        BigDecimal amount,
        String description
    ) {
        return new BankingTransaction(
            null,
            account,
            null,
            null,
            amount,
            description,
            type,
            BankingTransactionStatus.PENDING
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingCard card,
        BigDecimal amount,
        String description
    ) {
        return new BankingTransaction(
            null,
            card.getBankingAccount(),
            card,
            null,
            amount,
            description,
            type,
            BankingTransactionStatus.PENDING
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        BankingTransfer transfer,
        String description
    ) {
        return new BankingTransaction(
            null,
            account,
            null,
            transfer,
            transfer.getAmount(),
            description,
            type,
            BankingTransactionStatus.PENDING
        );
    }

    public Long getId() {
        return id;
    }

    public BankingAccount getBankingAccount() {
        return bankingAccount;
    }

    public Long getBankingAccountId() {
        return this.bankingAccount != null ? this.bankingAccount.getId() : null;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingTransactionType getType() {
        return type;
    }

    public BankingTransactionStatus getStatus() {
        return status;
    }

    private void setStatus(BankingTransactionStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingTransactionStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    public BankingTransfer getTransfer() {
        return transfer;
    }

    public Long getTransferId() {
        return transfer != null ? transfer.getId() : null;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public BankingCard getBankingCard() {
        return bankingCard;
    }

    public Long getBankingCardId() {
        return this.bankingCard != null ? this.bankingCard.getId() : null;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public boolean isOwnedBy(Long userId) {
        return Objects.equals(getBankingAccount().getOwner().getId(), userId);
    }

    private void calcBalanceBefore() {
        if (this.bankingAccount != null) {
            this.balanceBefore = this.bankingAccount.getBalance();
            return;
        }
        this.balanceBefore = BigDecimal.valueOf(0);
    }

    private void calcBalanceAfter() {
        switch (type) {
            case DEPOSIT, TRANSFER_FROM -> this.balanceAfter = balanceBefore.add(this.amount);
            case TRANSFER_TO, CARD_CHARGE, WITHDRAWAL -> this.balanceAfter = balanceBefore.subtract(this.amount);
            default -> this.balanceAfter = balanceBefore;
        }
    }

    public void capture() {
        this.assertPending();
        this.complete();
    }

    public void complete() {
        this.setStatus(BankingTransactionStatus.COMPLETED);
        this.updatedAt = Instant.now();
    }

    public void fail(String failReason) {
        this.description = failReason;
        this.setStatus(BankingTransactionStatus.FAILED);
        this.updatedAt = Instant.now();
    }

    public void assertPending() {
        if (status != BankingTransactionStatus.PENDING) {
            throw new BankingTransactionNotPendingStatusException(this.id);
        }
    }

    /**
     * Assert the ownership of the transaction.
     *
     * @param userId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransactionNotOwnerException if the transaction does not belong to the customer
     */
    public BankingTransaction assertOwnedBy(Long userId) {

        // compare account owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new BankingTransactionNotOwnerException(getId(), userId);
        }

        return this;
    }
}
