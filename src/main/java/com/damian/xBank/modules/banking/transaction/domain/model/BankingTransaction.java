package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotAuthorizedException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotPendingStatusException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @JoinColumn(name = "outgoing_transfer_id", referencedColumnName = "id")
    private OutgoingTransfer outgoingTransfer;

    @ManyToOne
    @JoinColumn(name = "incoming_transfer_id", referencedColumnName = "id")
    private IncomingTransfer incomingTransfer;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column
    private String description;

    @Column
    private String authorizationId;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private BankingTransactionType type;

    @Enumerated(EnumType.STRING)
    private BankingTransactionPaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private BankingTransactionStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public static BankingTransaction reconstitute(
        Long id,
        BankingAccount account,
        BankingCard card,
        OutgoingTransfer transfer,
        IncomingTransfer incomingTransfer,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description,
        String authorizationId,
        BankingTransactionType type,
        BankingTransactionStatus status,
        BankingTransactionPaymentStatus paymentStatus,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new BankingTransaction(
            id,
            account,
            card,
            transfer,
            incomingTransfer,
            amount,
            balanceBefore,
            balanceAfter,
            description,
            authorizationId,
            type,
            paymentStatus,
            status,
            createdAt,
            updatedAt
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        BigDecimal amount,
        String description
    ) {
        BankingTransaction transaction = new BankingTransaction(
            null,
            account,
            null,
            null,
            null,
            amount,
            null,
            null,
            description,
            null,
            type,
            BankingTransactionPaymentStatus.PENDING,
            BankingTransactionStatus.PENDING,
            Instant.now(),
            Instant.now()
        );

        transaction.calcBalanceBefore();
        transaction.calcBalanceAfter();
        return transaction;
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingCard card,
        BigDecimal amount,
        String description
    ) {
        BankingTransaction transaction = new BankingTransaction(
            null,
            card.getBankingAccount(),
            card,
            null,
            null,
            amount,
            null,
            null,
            description,
            null,
            type,
            BankingTransactionPaymentStatus.PENDING,
            BankingTransactionStatus.PENDING,
            Instant.now(),
            Instant.now()
        );

        transaction.calcBalanceBefore();
        transaction.calcBalanceAfter();
        return transaction;
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingCard card,
        BigDecimal amount,
        String description,
        String authorizationId
    ) {
        BankingTransaction transaction = new BankingTransaction(
            null,
            card.getBankingAccount(),
            card,
            null,
            null,
            amount,
            null,
            null,
            description,
            authorizationId,
            type,
            BankingTransactionPaymentStatus.AUTHORIZED,
            BankingTransactionStatus.PENDING,
            Instant.now(),
            Instant.now()
        );

        transaction.calcBalanceBefore();
        transaction.calcBalanceAfter();
        return transaction;
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        OutgoingTransfer outgoingTransfer,
        String description
    ) {
        BankingTransaction transaction = new BankingTransaction(
            null,
            account,
            null,
            outgoingTransfer,
            null,
            outgoingTransfer.getAmount(),
            null,
            null,
            description,
            null,
            type,
            BankingTransactionPaymentStatus.PENDING,
            BankingTransactionStatus.PENDING,
            Instant.now(),
            Instant.now()
        );

        transaction.calcBalanceBefore();
        transaction.calcBalanceAfter();
        return transaction;
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        IncomingTransfer incomingTransfer,
        String description
    ) {
        BankingTransaction transaction = new BankingTransaction(
            null,
            account,
            null,
            null,
            incomingTransfer,
            incomingTransfer.getAmount(),
            null,
            null,
            description,
            null,
            type,
            BankingTransactionPaymentStatus.PENDING,
            BankingTransactionStatus.PENDING,
            Instant.now(),
            Instant.now()
        );

        transaction.calcBalanceBefore();
        transaction.calcBalanceAfter();
        return transaction;
    }

    public Long getBankingAccountId() {
        return this.bankingAccount != null ? this.bankingAccount.getId() : null;
    }

    public Long getTransferId() {
        return outgoingTransfer != null ? outgoingTransfer.getId() : null;
    }

    public Long getBankingCardId() {
        return this.bankingCard != null ? this.bankingCard.getId() : null;
    }

    public boolean isOwnedBy(Long userId) {
        return Objects.equals(getBankingAccount().getOwner().getId(), userId);
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
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

    private void setPaymentStatus(BankingTransactionPaymentStatus newStatus) {
        if (this.paymentStatus == newStatus) {
            return;
        }

        if (!this.paymentStatus.canTransitionTo(newStatus)) {
            throw new BankingTransactionStatusTransitionException(
                this.id,
                this.paymentStatus.name(),
                newStatus.name()
            );
        }

        this.paymentStatus = newStatus;
        markAsUpdated();
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
            case DEPOSIT, INCOMING_TRANSFER -> this.balanceAfter = balanceBefore.add(this.amount);
            case OUTGOING_TRANSFER, CARD_CHARGE, WITHDRAWAL -> this.balanceAfter = balanceBefore.subtract(this.amount);
            default -> this.balanceAfter = balanceBefore;
        }
    }

    public void authorize() {
        this.assertPending();
        setPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED);
        this.authorizationId = generateAuthorizationId();
        complete();
    }

    public void capture() {
        this.assertAuthorized();
        setPaymentStatus(BankingTransactionPaymentStatus.CAPTURED);
    }

    public void complete() {
        this.setStatus(BankingTransactionStatus.COMPLETED);
        markAsUpdated();
    }

    public void reject(String rejectReason) {
        this.description = rejectReason;
        this.setStatus(BankingTransactionStatus.REJECTED);
        this.setPaymentStatus(BankingTransactionPaymentStatus.FAILED);
        markAsUpdated();
    }

    public void fail(String failReason) {
        this.description = failReason;
        this.setStatus(BankingTransactionStatus.FAILED);
        this.setPaymentStatus(BankingTransactionPaymentStatus.FAILED);
        markAsUpdated();
    }

    public void assertPending() {
        if (status != BankingTransactionStatus.PENDING) {
            throw new BankingTransactionNotPendingStatusException(this.id);
        }
    }

    public void assertOwnedBy(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new BankingTransactionNotOwnerException(getId(), userId);
        }
    }

    public void assertAuthorized() {
        if (this.getPaymentStatus() != BankingTransactionPaymentStatus.AUTHORIZED) {
            throw new BankingTransactionNotAuthorizedException(this.id);
        }
    }

    private static String generateAuthorizationId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "BankingTransaction{" +
               "id=" + id +
               ", bankingAccountId=" + bankingAccount.getId() +
               ", bankingCardId=" + bankingCard.getId() +
               ", outgoingTransferId=" + outgoingTransfer.getId() +
               ", incomingTransferId=" + incomingTransfer.getId() +
               ", amount=" + amount +
               ", balanceBefore=" + balanceBefore +
               ", balanceAfter=" + balanceAfter +
               ", description='" + description + '\'' +
               ", type=" + type +
               ", status=" + status +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
