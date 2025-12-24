package com.damian.xBank.modules.banking.transfer.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.transfer.domain.enums.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

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
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
        this.description = "";
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

    public BankingTransfer setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BankingAccount getToAccount() {
        return toAccount;
    }

    public BankingTransfer setToAccount(BankingAccount toAccount) {
        this.toAccount = toAccount;
        return this;
    }

    public BankingAccount getFromAccount() {
        return fromAccount;
    }

    public String getDescription() {
        return description;
    }

    public BankingTransfer setDescription(String description) {
        this.description = description;
        return this;
    }

    public BankingTransfer setFromAccount(BankingAccount fromAccount) {
        this.fromAccount = fromAccount;
        return this;
    }

    public boolean isOwnedBy(Long customerId) {

        // compare account owner id with given customer id
        return Objects.equals(getFromAccount().getOwner().getId(), customerId);
    }

    /**
     * Assert the ownership of the account belongs to {@link Customer}.
     *
     * @param customerId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransferNotOwnerException if the account does not belong to the customer
     */
    public BankingTransfer assertOwnedBy(Long customerId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(customerId)) {
            throw new BankingTransferNotOwnerException(getFromAccount().getOwner().getId(), customerId);
        }

        return this;
    }

    public void confirm() {
        this.status = BankingTransferStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void reject() {
        this.status = BankingTransferStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    /**
     * Validate that current account and {@code toBankingAccount} have the same currency
     *
     * @return the current validator instance for chaining
     * @throws BankingTransferCurrencyMismatchException if the account does not belong to the customer
     */
    public BankingTransfer assertCurrenciesMatch() {

        // if currencies are different, throw exception
        if (!Objects.equals(fromAccount.getAccountCurrency(), toAccount.getAccountCurrency())) {
            throw new BankingTransferCurrencyMismatchException(toAccount.getId());
        }

        return this;
    }

    /**
     * Validate that current account and {@code toBankingAccount} are not the same
     *
     * @return the current validator instance for chaining
     * @throws BankingTransferSameException if the account does not belong to the customer
     */
    public BankingTransfer assertDifferentAccounts() {

        // check bankingAccount and toBankingAccount are not the same
        if (Objects.equals(fromAccount.getId(), toAccount.getId())) {
            throw new BankingTransferSameException(toAccount.getId());
        }

        return this;
    }

    /**
     * Validate a transfer between current account and {@code toBankingAccount}.
     *
     * @return the current validator instance for chaining
     * @throws BankingTransferCurrencyMismatchException if the account does not belong to the customer
     * @throws BankingTransferSameException             if the account does not belong to the customer
     */ // TODO assertCanCarryOperation? look for better naming
    public BankingTransfer assertCanTransfer() {
        // TODO check amount
        //        this.fromAccount.assertSufficientFunds(amount);

        // check "account' and toBankingAccount are not the same
        this.assertDifferentAccounts();

        // check currency are the same on both accounts
        this.assertCurrenciesMatch();

        // check if the source account is active
        fromAccount.assertActive();

        // check if the destiny account is active
        toAccount.assertActive();

        return this;
    }
}
