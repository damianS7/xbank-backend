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
 * Caso de uso que representa el retiro de fondos de un cajero ATM.
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
     * @param command Comando con los datos necesario para la operación
     * @return Resultado con la transacción
     */
    @Transactional
    public WithdrawFromATMResult execute(WithdrawFromATMCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // La tarjeta que realiza el retiro
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Comprobaciones de seguridad
        bankingCard.assertCanSpend(currentUser, command.amount(), command.pin());

        BankingTransaction transaction = BankingTransaction.create(
            BankingTransactionType.WITHDRAWAL,
            bankingCard,
            command.amount(),
            "ATM Withdrawal."
        );

        bankingCard.withdraw(command.amount());
        transaction.complete();
        bankingTransactionRepository.save(transaction);

        // Notificar al usuario
        notificationPublisher.publish(
            notificationEventFactory.withdrawCompleted(transaction)
        );

        return WithdrawFromATMResult.from(transaction);
    }
}