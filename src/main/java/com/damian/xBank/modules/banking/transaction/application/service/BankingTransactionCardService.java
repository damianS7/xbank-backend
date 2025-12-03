package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infra.repository.BankingTransactionRepository;
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
