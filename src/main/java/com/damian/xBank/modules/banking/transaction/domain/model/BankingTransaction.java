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

    protected BankingTransaction() {
        this.amount = BigDecimal.valueOf(0);
        this.status = BankingTransactionStatus.PENDING;
        this.paymentStatus = BankingTransactionPaymentStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    BankingTransaction(
        Long transactionId,
        BankingAccount bankingAccount,
        BankingCard bankingCard,
        OutgoingTransfer outgoingTransfer,
        IncomingTransfer incomingTransfer,
        BigDecimal amount,
        String description,
        BankingTransactionType type,
        BankingTransactionStatus status,
        BankingTransactionPaymentStatus paymentStatus,
        String authorizationId
    ) {
        this();
        this.id = transactionId;
        this.bankingAccount = bankingAccount;
        this.bankingCard = bankingCard;
        this.outgoingTransfer = outgoingTransfer;
        this.incomingTransfer = incomingTransfer;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.authorizationId = authorizationId;
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
            null,
            amount,
            description,
            type,
            BankingTransactionStatus.PENDING,
            BankingTransactionPaymentStatus.PENDING,
            null
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
            null,
            amount,
            description,
            type,
            BankingTransactionStatus.PENDING,
            BankingTransactionPaymentStatus.PENDING,
            null
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingCard card,
        BigDecimal amount,
        String description,
        String authorizationId
    ) {
        return new BankingTransaction(
            null,
            card.getBankingAccount(),
            card,
            null,
            null,
            amount,
            description,
            type,
            BankingTransactionStatus.PENDING,
            BankingTransactionPaymentStatus.AUTHORIZED,
            authorizationId
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        OutgoingTransfer transfer,
        String description
    ) {
        return new BankingTransaction(
            null,
            account,
            null,
            transfer,
            null,
            transfer.getAmount(),
            description,
            type,
            BankingTransactionStatus.PENDING,
            BankingTransactionPaymentStatus.PENDING,
            null
        );
    }

    public static BankingTransaction create(
        BankingTransactionType type,
        BankingAccount account,
        IncomingTransfer incomingTransfer,
        String description
    ) {
        return new BankingTransaction(
            null,
            account,
            null,
            null,
            incomingTransfer,
            incomingTransfer.getAmount(),
            description,
            type,
            BankingTransactionStatus.PENDING,
            BankingTransactionPaymentStatus.PENDING,
            null
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

    public OutgoingTransfer getOutgoingTransfer() {
        return outgoingTransfer;
    }

    public Long getTransferId() {
        return outgoingTransfer != null ? outgoingTransfer.getId() : null;
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

    // TODO hacer authorize en el usecase!!!
    public void authorize(String authorizationId) {
        this.assertPending();
        setPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED);
        this.authorizationId = authorizationId;
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

    public String getAuthorizationId() {
        return authorizationId;
    }

    public BankingTransactionPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}
