package com.damian.xBank.modules.banking.transaction.service;

import com.damian.xBank.modules.banking.card.model.BankingCard;
import com.damian.xBank.modules.banking.card.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.repository.BankingTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingTransactionCardService {
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final BankingTransactionAccountService bankingTransactionAccountService;

    public BankingTransactionCardService(
            BankingCardRepository bankingCardRepository,
            BankingTransactionRepository bankingTransactionRepository,
            BankingTransactionAccountService bankingTransactionAccountService
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.bankingTransactionAccountService = bankingTransactionAccountService;
    }

    public Page<BankingTransaction> getTransactions(Long bankingCardId, Pageable pageable) {
        return bankingTransactionRepository.findByBankingCardId(bankingCardId, pageable);
    }

    public BankingTransaction createTransaction(
            BankingCard bankingCard,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        return bankingTransactionAccountService.createTransaction(
                bankingCard.getAssociatedBankingAccount(),
                transactionType,
                amount,
                description
        );
    }

    public BankingTransaction generateTransaction(
            BankingCard bankingCard,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction transaction = bankingTransactionAccountService.createTransaction(
                bankingCard.getAssociatedBankingAccount(),
                transactionType,
                amount,
                description
        );

        return bankingTransactionAccountService.persistTransaction(transaction);
    }
}
