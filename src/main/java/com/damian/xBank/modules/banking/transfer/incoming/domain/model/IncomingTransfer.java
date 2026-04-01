package com.damian.xBank.modules.banking.transfer.incoming.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.incoming.domain.exception.IncomingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.incoming.domain.exception.IncomingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.incoming.domain.exception.IncomingTransferStatusTransitionException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "incoming_transfers")
public class IncomingTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAccountIban;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private BankingAccount toAccount;

    @Column(nullable = false)
    private String toAccountIban;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncomingTransferStatus status;

    @Column(name = "provider_authorization_id")
    private String providerAuthorizationId;

    @Column(nullable = false)
    private String reference;

    @OneToOne(
        mappedBy = "incomingTransfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private BankingTransaction transaction;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    protected IncomingTransfer() {
        this.status = IncomingTransferStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
        this.reference = "";
    }

    private IncomingTransfer(
        Long transferId,
        String fromAccountIban,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        String reference
    ) {
        this();
        this.id = transferId;
        this.fromAccountIban = fromAccountIban;
        this.toAccount = toAccount;
        this.toAccountIban = toAccountIban;
        this.amount = amount;
        this.reference = reference;

        if (this.toAccount != null) {
            this.toAccountIban = this.toAccount.getAccountNumber();
        }

        // Assert que la transferencia se puede realizar
        this.assertTransferPossible();

        // Genera las transacciones asociadas
        this.generateTransactions();
    }

    public static IncomingTransfer create(
        String fromAccountIban,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        String reference
    ) {
        return new IncomingTransfer(
            null,
            fromAccountIban,
            toAccount,
            toAccountIban,
            amount,
            reference
        );
    }

    public Long getId() {
        return id;
    }

    public IncomingTransferStatus getStatus() {
        return status;
    }

    public String getToAccountIban() {
        return toAccountIban;
    }

    public String getProviderAuthorizationId() {
        return providerAuthorizationId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingAccount getToAccount() {
        return toAccount;
    }

    public String getFromAccountIban() {
        return fromAccountIban;
    }

    public String getReference() {
        return reference;
    }

    public BankingTransaction getTransaction() {
        return transaction;
    }

    public boolean isOwnedBy(Long userId) {
        return Objects.equals(toAccount.getOwner().getId(), userId);
    }

    private void setStatus(IncomingTransferStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new IncomingTransferStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    /**
     * Genera las transacciones asociadas a la transferencia
     */
    private void generateTransactions() {
        // Si la transferencia es a una cuenta interna ...
        if (toAccount != null) {
            this.transaction = BankingTransaction.createIncomingTransferTransaction(
                BankingTransactionType.INCOMING_TRANSFER,
                toAccount,
                this,
                "Transfer from " + reference
            );
        }
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void fail(String failedReason) {
        this.transaction.fail(failedReason);
        this.setStatus(IncomingTransferStatus.FAILED);
    }

    public void authorize(String providerAuthorizationId) {
        this.providerAuthorizationId = providerAuthorizationId;
        this.setStatus(IncomingTransferStatus.AUTHORIZED);
    }

    /**
     * Paso final que completa la transferencia
     */
    public void complete() {
        // Añade los fondos a la cuenta destino
        this.toAccount.deposit(amount);

        // Completa la transacción
        this.transaction.complete();

        // Cambia el estado de la transferencia
        this.setStatus(IncomingTransferStatus.COMPLETED);
    }

    /**
     * Válida que ambas cuentas tienen la misma moneda
     *
     * @throws IncomingTransferCurrencyMismatchException Si las cuentas usan diferente moneda
     */
    public void assertCurrenciesMatch() {
        // if currencies are different, throw exception
        if (toAccount.getCurrency() != toAccount.getCurrency()) {
            throw new IncomingTransferCurrencyMismatchException(toAccount.getId());
        }
    }

    /**
     * Válida que la transferencia sea posible
     *
     * @throws IncomingTransferCurrencyMismatchException Si las cuentas usan diferente moneda
     */
    public void assertTransferPossible() {
        toAccount.assertActive();

        if (toAccount != null) {
            toAccount.assertActive();
            this.assertCurrenciesMatch();
        }
    }

    /**
     * Válida que la cuenta pertenezca al userId
     *
     * @param userId El ID del usuario a comprobar
     * @throws IncomingTransferNotOwnerException Si la cuenta no pertenece al usuario
     */
    public void assertOwnedBy(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IncomingTransferNotOwnerException(toAccount.getOwner().getId(), userId);
        }
    }

    @Override
    public String toString() {
        return "IncomingTransfer{" +
               "id=" + id +
               ", fromAccountIban='" + fromAccountIban + '\'' +
               ", toAccountId=" + toAccount.getId() +
               ", toAccountIban='" + toAccountIban + '\'' +
               ", amount=" + amount +
               ", status=" + status +
               ", providerAuthorizationId='" + providerAuthorizationId + '\'' +
               ", reference='" + reference + '\'' +
               ", transactionId=" + transaction.getId() +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
