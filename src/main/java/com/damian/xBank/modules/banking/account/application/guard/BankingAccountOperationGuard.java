package com.damian.xBank.modules.banking.account.application.guard;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountDepositException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountTransferException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountTransferSameAccountException;

import java.util.Objects;

public class BankingAccountOperationGuard {
    private final BankingAccount account;

    public BankingAccountOperationGuard(BankingAccount account) {
        this.account = account;
    }

    public static BankingAccountOperationGuard forAccount(BankingAccount account) {
        return new BankingAccountOperationGuard(account);
    }

    /**
     * Validate that both {@link #account} and {@code toBankingAccount} have the same currency
     *
     * @param toBankingAccount the destination account to check
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferCurrencyMismatchException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard assertCurrenciesMatch(BankingAccount toBankingAccount) {

        // if currencies are different, throw exception
        if (!Objects.equals(account.getAccountCurrency(), toBankingAccount.getAccountCurrency())) {
            throw new BankingAccountTransferCurrencyMismatchException(toBankingAccount.getId());
        }

        return this;
    }

    /**
     * Validate that {@link #account} and {@code toBankingAccount} are not the same
     *
     * @param toBankingAccount the destination account to check
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferSameAccountException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard assertDifferentAccounts(BankingAccount toBankingAccount) {

        // check bankingAccount and toBankingAccount are not the same
        if (account.getId().equals(toBankingAccount.getId())) {
            throw new BankingAccountTransferSameAccountException(toBankingAccount.getId());
        }

        return this;
    }

    /**
     * Validate a transfer between {@link #account} and {@code toBankingAccount}.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountTransferException if the account does not belong to the customer
     */
    public BankingAccountOperationGuard assertCanTransfer(
            BankingAccount toBankingAccount
    ) {
        // check "account' and toBankingAccount are not the same
        this.assertDifferentAccounts(toBankingAccount);

        // check currency are the same on both accounts
        this.assertCurrenciesMatch(toBankingAccount);

        // check the account status and see if can be used to operate
        BankingAccountGuard.forAccount(account)
                           .assertActive();

        BankingAccountGuard.forAccount(toBankingAccount)
                           .assertActive();

        return this;
    }

    /**
     * Assert that deposit can be carried out into {@link #account}.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountDepositException if the deposit can not be carried out
     */
    public BankingAccountOperationGuard assertCanDeposit() {

        // check the account status and see if can be used to operate
        BankingAccountGuard.forAccount(account)
                           .assertActive();

        return this;
    }

}