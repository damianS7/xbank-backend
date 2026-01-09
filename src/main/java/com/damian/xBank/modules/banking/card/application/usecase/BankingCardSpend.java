package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.factory.NotificationFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingCardSpend {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final NotificationFactory notificationFactory;
    private final NotificationPublisher notificationPublisher;
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;

    public BankingCardSpend(
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            NotificationFactory notificationFactory,
            NotificationPublisher notificationPublisher,
            BankingCardRepository bankingCardRepository,
            BankingTransactionPersistenceService bankingTransactionPersistenceService
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.notificationFactory = notificationFactory;
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.notificationPublisher = notificationPublisher;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Spend money from a card
     *
     * @param bankingCardId the id of the card to spend from
     * @param request       the request with the data needed to perfom the operation
     * @return the created transaction
     */
    @Transactional
    public BankingTransaction execute(Long bankingCardId, BankingCardSpendRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );


        // run validations for the card and throw exception
        bankingCard.assertCanSpend(currentUser, request.amount(), request.cardPIN());

        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        bankingCard.getBankingAccount(),
                        request.amount()
                )
                .setBankingCard(bankingCard)
                .setStatus(BankingTransactionStatus.COMPLETED)
                .setDescription(request.description());

        bankingCard.chargeAmount(request.amount());

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);

        // Notify the user
        notificationPublisher.publish(
                notificationFactory.cardPaymentCompleted(transaction)
        );

        return transaction;
    }
}