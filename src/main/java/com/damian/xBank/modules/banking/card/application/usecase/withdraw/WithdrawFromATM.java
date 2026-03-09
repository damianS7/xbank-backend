package com.damian.xBank.modules.banking.card.application.usecase.withdraw;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
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
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;

    public WithdrawFromATM(
        AuthenticationContext authenticationContext,
        BankingCardRepository bankingCardRepository,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory,
        BankingTransactionPersistenceService bankingTransactionPersistenceService
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
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

        BankingTransaction transaction = BankingTransaction
            .create(
                BankingTransactionType.WITHDRAWAL,
                bankingCard.getBankingAccount(),
                command.amount()
            )
            .setBankingCard(bankingCard)
            .setStatus(BankingTransactionStatus.COMPLETED)
            .setDescription("ATM Withdrawal.");

        bankingCard.chargeAmount(command.amount());

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);

        // Notify the user
        notificationPublisher.publish(
            notificationEventFactory.withdrawCompleted(transaction)
        );

        // save the data and return BankingAccount
        //        return bankingCardRepository.save(bankingCard);

        return WithdrawFromATMResult.from(transaction);
    }
}