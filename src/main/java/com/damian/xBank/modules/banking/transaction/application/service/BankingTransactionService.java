package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BankingTransactionService {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionService(
            BankingAccountRepository bankingAccountRepository,
            BankingCardRepository bankingCardRepository,
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingCardRepository = bankingCardRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a single transaction by id.
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

            // assert transaction belongs to him
            transaction.assertOwnedBy(currentCustomer.getId());

        }

        return transaction;
    }

    /**
     * Returns a paginated result containing the transactions from a banking account.
     *
     * @param accountId
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> getAccountTransactions(Long accountId, Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingAccount account = bankingAccountRepository
                .findById(accountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(accountId)
                );

        // if the current user is a customer ...
        if (currentCustomer.hasRole(UserAccountRole.CUSTOMER)) {

            // assert account belongs to him
            account.assertOwnedBy(currentCustomer.getId());

        }

        return bankingTransactionRepository.findByBankingAccountId(accountId, pageable);
    }

    /**
     * Returns a paginated result containing the transactions from a card.
     *
     * @param cardId
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> getCardTransactions(Long cardId, Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingCard card = bankingCardRepository
                .findById(cardId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(cardId)
                );

        // if the current user is a customer ...
        if (currentCustomer.hasRole(UserAccountRole.CUSTOMER)) {

            // assert account belongs to him
            card.assertOwnedBy(currentCustomer.getId());

        }

        return bankingTransactionRepository.findByBankingCardId(cardId, pageable);
    }

    /**
     * Returns a paginated result containing the pending transactions from current customer.
     *
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> getAccountPendingTransactions(Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        return bankingTransactionRepository.findByStatusAndBankingAccount_Customer_Id(
                BankingTransactionStatus.PENDING,
                currentCustomer.getId(),
                pageable
        );
    }

    /**
     * Stores a banking account transaction by adding it to the owner's account
     * and persisting the account.
     *
     * @param transaction the banking account transaction to store
     * @return the stored banking account transaction
     */
    @Transactional
    public BankingTransaction record(BankingTransaction transaction) {
        final BankingAccount bankingAccount = transaction.getBankingAccount();
        transaction.setUpdatedAt(Instant.now());

        // Add the transaction to the owners account
        bankingAccount.addTransaction(transaction);

        // Record and return the stored transaction
        return bankingTransactionRepository.save(transaction);
    }

    public BankingTransaction record(
            BankingAccount account,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {

        BankingTransaction transaction = BankingTransaction
                .create(
                        transactionType,
                        account,
                        amount
                )
                .setDescription(description);

        return this.record(transaction);
    }

    public BankingTransaction record(
            BankingCard card,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {

        BankingTransaction transaction = BankingTransaction
                .create(
                        transactionType,
                        card.getBankingAccount(),
                        amount
                )
                .setBankingCard(card)
                .setDescription(description);

        return this.record(transaction);
    }

    public BankingTransaction complete(BankingTransaction transaction) {
        transaction.complete();
        return bankingTransactionRepository.save(transaction);
    }

    public BankingTransaction reject(BankingTransaction transaction) {
        transaction.reject();
        return bankingTransactionRepository.save(transaction);
    }
}