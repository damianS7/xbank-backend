package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.application.guard.BankingTransactionGuard;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingTransactionCardService {
    private final AuthenticationContext authenticationContext;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final BankingTransactionAccountService bankingTransactionAccountService;

    public BankingTransactionCardService(
            AuthenticationContext authenticationContext,
            BankingTransactionRepository bankingTransactionRepository,
            BankingTransactionAccountService bankingTransactionAccountService
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.bankingTransactionAccountService = bankingTransactionAccountService;
    }

    public Page<BankingTransaction> getTransactions(Long bankingCardId, Pageable pageable) {
        return bankingTransactionRepository.findByBankingCardId(bankingCardId, pageable);
    }

    /**
     * Creates a new transaction
     *
     * @param bankingCard
     * @param transactionType
     * @param amount
     * @param description
     * @return
     */
    public BankingTransaction buildTransaction(
            BankingCard bankingCard,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        return bankingTransactionAccountService.buildTransaction(
                bankingCard.getBankingAccount(),
                transactionType,
                amount,
                description
        );
    }

    /**
     * Generate a new transaction and stored into db.
     *
     * @param bankingCard
     * @param transactionType
     * @param amount
     * @param description
     * @return
     */
    public BankingTransaction generateTransaction(
            BankingCard bankingCard,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction transaction = bankingTransactionAccountService.buildTransaction(
                bankingCard.getBankingAccount(),
                transactionType,
                amount,
                description
        );

        // check card has funds
        bankingCard.assertSufficientFunds(amount);

        return bankingTransactionAccountService.recordTransaction(transaction);
    }

    /**
     * This method will confirm a transaction
     * Card transactions must be confirmer through the app.
     *
     * @param transactionId
     * @return BankingTransaction confirmed transaction
     */
    public BankingTransaction confirmTransaction(Long transactionId) {
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingTransaction transaction = bankingTransactionRepository
                .findById(transactionId)
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(transactionId)
                );


        // run validations
        BankingTransactionGuard.forTransaction(transaction)
                               .assertOwnership(currentCustomer);

        // deduct money from card
        BankingCard card = transaction.getBankingCard();
        card.chargeAmount(transaction.getAmount());

        // After amount is deducted, mark the transaction as completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        return bankingTransactionRepository.save(transaction);
    }
}
