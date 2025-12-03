package com.damian.xBank.modules.banking.account.application.guard;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;

import java.math.BigDecimal;

// TODO renamed to BankingAccountGuard or similar
public class BankingAccountOperationGuard {
    private final BankingAccount account;

    public BankingAccountOperationGuard(BankingAccount account) {
        this.account = account;
    }

    public static BankingAccountOperationGuard validate(BankingAccount account) {
        return new BankingAccountOperationGuard(account);
    }

    /**
     * Validate the ownership of the {@link #account}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingAccountOwnershipException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard ownership(Customer customer) {

        // compare account owner id with given customer id
        if (!account.getOwner().getId().equals(customer.getId())) {
            throw new BankingAccountOwnershipException(
                    Exceptions.BANKING.ACCOUNT.OWNERSHIP, account.getId(), customer.getId()
            );
        }

        return this;
    }

    /**
     * Validate if the {@link #account} has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingAccountInsufficientFundsException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard sufficientFunds(BigDecimal amount) {

        // check if account has enough funds
        if (!account.hasEnoughFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(
                    Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS,
                    account.getId()
            );
        }

        return this;
    }

    /**
     * Validate that {@link #account} and {@code toBankingAccount} are not the same
     *
     * @param toBankingAccount the destination account to check
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferException if the account does not belong to the customer
     */
    private BankingAccountOperationGuard differentDestination(BankingAccount toBankingAccount) {

        // check bankingAccount and toBankingAccount are not the same
        if (account.getId().equals(toBankingAccount.getId())) {
            throw new BankingAccountTransferException(
                    Exceptions.BANKING.ACCOUNT.SAME_DESTINATION,
                    account.getId(),
                    toBankingAccount.getId()
            );
        }

        return this;
    }

    /**
     * Validate that both {@link #account} and {@code toBankingAccount} have the same currency
     *
     * @param toBankingAccount the destination account to check
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferException if the account does not belong to the customer
     */
    private BankingAccountOperationGuard sameCurrency(BankingAccount toBankingAccount) {

        // if currencies are different, throw exception
        if (!account.getAccountCurrency().equals(toBankingAccount.getAccountCurrency())) {
            throw new BankingAccountTransferException(
                    Exceptions.BANKING.TRANSACTION.DIFFERENT_CURRENCY,
                    account.getId(),
                    toBankingAccount.getId()
            );
        }

        return this;
    }

    /**
     * Validate {@link #account} is not SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard notSuspended() {

        final boolean isAccountSuspended = account.getAccountStatus().equals(BankingAccountStatus.SUSPENDED);

        // check if account is SUSPENDED
        if (isAccountSuspended) {
            throw new BankingAccountSuspendedException(account.getId());
        }

        return this;
    }

    /**
     * Validate {@link #account} is not CLOSED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard notClosed() {
        final boolean isAccountClosed = account.getAccountStatus().equals(BankingAccountStatus.CLOSED);

        // check if account is CLOSED
        if (isAccountClosed) {
            throw new BankingAccountClosedException(
                    account.getId()
            );
        }

        return this;
    }

    /**
     * Validate {@link #account} is not CLOSED or SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard active() {
        this.notSuspended();
        this.notClosed();
        return this;
    }

    // TODO review this

    /**
     * Validate a transfer between {@link #account} and {@code toBankingAccount}.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard transfer(
            BankingAccount toBankingAccount,
            BigDecimal amount
    ) {
        // check "account' and toBankingAccount are not the same
        this.differentDestination(toBankingAccount);

        // check currency are the same
        this.sameCurrency(toBankingAccount);

        // check the funds from the sender account
        this.sufficientFunds(amount);

        // check the account status and see if can be used to operate
        this.active();

        BankingAccountOperationGuard.validate(toBankingAccount)
                                    .active();

        return this;
    }

    // TODO: add withdrawal validation method
    // TODO: add deposit validation method

}