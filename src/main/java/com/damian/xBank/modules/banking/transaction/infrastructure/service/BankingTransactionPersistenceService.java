package com.damian.xBank.modules.banking.transaction.infrastructure.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BankingTransactionPersistenceService {
    private final BankingTransactionRepository bankingTransactionRepository;

    public BankingTransactionPersistenceService(
            BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
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
}