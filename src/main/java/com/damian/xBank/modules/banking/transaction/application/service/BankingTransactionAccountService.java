package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service // TODO
public class BankingTransactionAccountService {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionAccountService(
            BankingAccountRepository bankingAccountRepository,
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Builds a transaction
     *
     * @param bankingAccount
     * @param transactionType
     * @param amount
     * @param description
     * @return
     */
    public BankingTransaction buildTransaction(
            BankingAccount bankingAccount,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        return BankingTransaction.create()
                                 .setBankingAccount(bankingAccount)
                                 .setType(transactionType)
                                 .setBalanceBefore(bankingAccount.getBalance())
                                 .setAmount(amount)
                                 .setDescription(description);
    }

    /**
     * Returns a paginated result containing the transactions from a banking account.
     *
     * @param bankingAccountId
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> getTransactions(Long bankingAccountId, Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingAccount account = bankingAccountRepository
                .findById(bankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(bankingAccountId)
                );

        // validate account belongs to
        account.assertOwnedBy(currentCustomer.getId());

        return bankingTransactionRepository.findByBankingAccountId(bankingAccountId, pageable);
    }

    /**
     * Returns a paginated result containing the pending transactions from current customer.
     *
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> getPendingTransactions(Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        return bankingTransactionRepository.findByStatusAndBankingAccount_Customer_Id(
                BankingTransactionStatus.PENDING,
                currentCustomer.getId(),
                pageable
        );
    }

    /**
     * Builds and record a transaction
     *
     * @param bankingAccount
     * @param transactionType
     * @param amount
     * @param description
     * @return
     */
    public BankingTransaction generateTransaction(
            BankingAccount bankingAccount,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        // createTransaction
        BankingTransaction transaction = this
                .buildTransaction(bankingAccount, transactionType, amount, description);

        // records the transaction into db
        this.recordTransaction(transaction);

        return transaction;
    }

    /**
     * Stores a banking account transaction by adding it to the owner's account and persisting the account.
     *
     * @param transaction the banking account transaction to store
     * @return the stored banking account transaction
     */
    public BankingTransaction recordTransaction(BankingTransaction transaction) {
        final BankingAccount bankingAccount = transaction.getBankingAccount();
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        // Add the transaction to the owners account
        bankingAccount.addTransaction(transaction);

        // Records the owner's account with the new transaction
        // Return the stored transaction
        return bankingTransactionRepository.save(transaction);
    }

    /**
     * Returns a transaction
     *
     * @param transactionId
     * @return requested transaction
     */
    public BankingTransaction getTransaction(Long transactionId) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingTransaction transaction = bankingTransactionRepository
                .findById(transactionId)
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(transactionId)
                );

        // if the current user is a customer ...
        if (currentCustomer.hasRole(UserAccountRole.CUSTOMER)) {

            // check transactions belongs to user
            transaction.assertOwnedBy(currentCustomer.getId());

        }

        return transaction;
    }

    /**
     * It changes the status of the transaction
     *
     * @param bankingTransactionId
     * @param status               the new status to set
     * @return the updated transaction
     */
    public BankingTransaction updateStatus(
            Long bankingTransactionId,
            BankingTransactionStatus status
    ) {
        // transaction to update
        final BankingTransaction bankingTransaction = bankingTransactionRepository
                .findById(bankingTransactionId)
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(
                                bankingTransactionId
                        )
                );

        // Validate (inside updateStatus method) and set the new status
        bankingTransaction.updateStatus(status);

        // we change the updateAt timestamp field
        bankingTransaction.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingTransactionRepository.save(bankingTransaction);
    }
}