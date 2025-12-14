package com.damian.xBank.modules.banking.account.application.guard;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;

import java.math.BigDecimal;
import java.util.Objects;

public class BankingAccountGuard {
    private final BankingAccount account;

    public BankingAccountGuard(BankingAccount account) {
        this.account = account;
    }

    public static BankingAccountGuard forAccount(BankingAccount account) {
        return new BankingAccountGuard(account);
    }

    /**
     * Validate the ownership of the {@link #account}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingAccountOwnershipException if the account does not belong to the customer
     */
    public BankingAccountGuard assertOwnership(Customer customer) {

        // compare account owner id with given customer id
        if (!Objects.equals(account.getOwner().getId(), customer.getId())) {
            throw new BankingAccountOwnershipException(
                    Exceptions.BANKING.ACCOUNT.OWNERSHIP, account.getId(), customer.getId()
            );
        }

        return this;
    }

    /**
     * Validate if the account has funds {@link #account}.
     *
     * @param amount the amount to check against
     * @return the current validator instance for chaining
     * @throws BankingAccountInsufficientFundsException if the account does not have funds
     */
    public BankingAccountGuard assertSufficientFunds(BigDecimal amount) {

        if (!account.hasSufficientFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(
                    Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS, account.getId()
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
    public BankingAccountGuard assertNotSuspended() {

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
    public BankingAccountGuard assertNotClosed() {
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
    public BankingAccountGuard assertActive() {
        this.assertNotSuspended();
        this.assertNotClosed();
        return this;
    }

}