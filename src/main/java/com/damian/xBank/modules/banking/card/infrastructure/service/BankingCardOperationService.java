package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingCardOperationService {

    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;
    private final BankingCardRepository bankingCardRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthenticationContext authenticationContext;

    public BankingCardOperationService(
            BankingTransactionPersistenceService bankingTransactionPersistenceService,
            BankingCardRepository bankingCardRepository,
            NotificationPublisher notificationPublisher,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.authenticationContext = authenticationContext;
    }


    /**
     * Handle the core logic of charging an amount to a card.
     * It performs all necessary validations and creates the transaction.
     * <p>
     * It's private to ensure that all card operations go through the public methods.
     *
     * @param card
     * @param cardPin
     * @param amount
     * @param description
     * @param transactionType
     * @return the created transaction
     */
    public BankingTransaction executeOperation(
            BankingCard card,
            String cardPin,
            BigDecimal amount,
            String description,
            BankingTransactionType transactionType
    ) {
        final Customer customerLogged = authenticationContext.getCurrentCustomer();

        // run validations for the card and throw exception
        card.assertOwnedBy(customerLogged.getId())
            .assertUsable()
            .assertCorrectPin(cardPin)
            .assertSufficientFunds(amount);

        BankingTransaction transaction = BankingTransaction
                .create(
                        transactionType,
                        card.getBankingAccount(),
                        amount
                )
                .setBankingCard(card)
                .setDescription(description);

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);


        return transaction;
    }
}
