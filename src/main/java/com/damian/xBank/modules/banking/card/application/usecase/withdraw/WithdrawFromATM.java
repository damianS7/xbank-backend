package com.damian.xBank.modules.banking.card.application.usecase.withdraw;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Withdraw money from ATM using banking card.
 *
 */
@Service
public class WithdrawFromATM {
    private final AuthenticationContext authenticationContext;
    private final BankingCardRepository bankingCardRepository;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;
    private final BankingTransactionRepository bankingTransactionRepository;

    public WithdrawFromATM(
        AuthenticationContext authenticationContext,
        BankingCardRepository bankingCardRepository,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory,
        BankingTransactionRepository bankingTransactionRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    /**
     * Withdraw money from ATM
     *
     * @param command the command with the data needed to perform the operation
     * @return the created transaction
     */
    @Transactional
    public WithdrawFromATMResult execute(WithdrawFromATMCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(
                () -> new BankingCardNotFoundException(command.cardId())
            );

        // run validations for the card and throw exception
        bankingCard.assertCanSpend(currentUser, command.amount(), command.pin());

        BankingTransaction transaction = BankingTransaction.create(
            BankingTransactionType.WITHDRAWAL,
            bankingCard,
            command.amount(),
            "ATM Withdrawal."
        );

        bankingCard.chargeAmount(command.amount());
        transaction.complete();
        bankingTransactionRepository.save(transaction);

        // Notify the user
        notificationPublisher.publish(
            notificationEventFactory.withdrawCompleted(transaction)
        );

        return WithdrawFromATMResult.from(transaction);
    }
}