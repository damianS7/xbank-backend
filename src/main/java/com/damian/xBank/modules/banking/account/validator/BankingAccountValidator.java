package com.damian.xBank.modules.banking.account.validator;

import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.exception.BankingAccountException;
import com.damian.xBank.modules.banking.account.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.exception.BankingAccountOwnershipException;
import com.damian.xBank.modules.banking.account.exception.BankingAccountTransferException;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;

import java.math.BigDecimal;

public class BankingAccountValidator {
    private final BankingAccount account;

    public BankingAccountValidator(BankingAccount account) {
        this.account = account;
    }

    public static BankingAccountValidator validate(BankingAccount account) {
        return new BankingAccountValidator(account);
    }

    /**
     * Validate the ownership of the {@link #account}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingAccountOwnershipException if the account does not belong to the customer
     */
    public BankingAccountValidator ownership(Customer customer) {

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
     * @throws BankingAccountOwnershipException if the account does not belong to the customer
     */
    public BankingAccountValidator sufficientFunds(BigDecimal amount) {

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
    private BankingAccountValidator differentDestination(BankingAccount toBankingAccount) {

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
    private BankingAccountValidator sameCurrency(BankingAccount toBankingAccount) {

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
     * Validate {@link #account} is not CLOSED or SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountException if the account does not belong to the customer
     */
    public BankingAccountValidator active() {
        final boolean isAccountClosed = account.getAccountStatus().equals(BankingAccountStatus.CLOSED);

        // check if account is CLOSED
        if (isAccountClosed) {
            throw new BankingAccountException(
                    Exceptions.BANKING.ACCOUNT.CLOSED, account.getId()
            );
        }

        final boolean isAccountSuspended = account.getAccountStatus().equals(BankingAccountStatus.SUSPENDED);

        // check if account is SUSPENDED
        if (isAccountSuspended) {
            throw new BankingAccountException(
                    Exceptions.BANKING.ACCOUNT.SUSPENDED, account.getId()
            );
        }

        return this;
    }

    // TODO review this

    /**
     * Validate a transfer between {@link #account} and {@code toBankingAccount}.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferException if the account does not belong to the customer
     */
    public BankingAccountValidator transfer(
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

        BankingAccountValidator.validate(toBankingAccount)
                               .active();

        return this;
    }

    // TODO: add withdrawal validation method
    // TODO: add deposit validation method

}