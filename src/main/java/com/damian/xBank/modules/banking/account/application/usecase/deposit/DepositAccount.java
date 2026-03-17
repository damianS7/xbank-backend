package com.damian.xBank.modules.banking.account.application.usecase.deposit;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountDepositNotAdminException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para depositar en una cuenta bancaria por parte de un administrador o personal del banco.
 */
@Service
public class DepositAccount {
    private static final Logger log = LoggerFactory.getLogger(DepositAccount.class);
    private final NotificationEventFactory notificationEventFactory;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final NotificationPublisher notificationPublisher;
    private final BankingTransactionRepository bankingTransactionRepository;

    public DepositAccount(
        NotificationEventFactory notificationEventFactory,
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        NotificationPublisher notificationPublisher,
        BankingTransactionRepository bankingTransactionRepository
    ) {
        this.notificationEventFactory = notificationEventFactory;
        this.notificationPublisher = notificationPublisher;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    /**
     * @param command Comando con los datos requeridos
     * @return Los datos del depósito
     */
    @Transactional
    public DepositAccountResult execute(DepositAccountCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Si el usuario actual no es admin ...
        if (!currentUser.isAdmin()) {
            throw new BankingAccountDepositNotAdminException(
                command.bankingAccountId(), currentUser.getId()
            );
        }

        // La cuenta donde se va a despositar
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.bankingAccountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(command.bankingAccountId())
            );

        // Comprobar que la cuenta está activa y puede recibir fondos
        bankingAccount.assertActive();

        // Crear la transacción asociada
        BankingTransaction transaction = BankingTransaction.create(
            BankingTransactionType.DEPOSIT,
            bankingAccount,
            command.amount(),
            "DEPOSIT by " + command.depositorName()
        );

        bankingAccount.deposit(command.amount());
        transaction.complete();
        bankingTransactionRepository.save(transaction);

        // Notificar al usuario receptor
        notificationPublisher.publish(
            notificationEventFactory.depositCompleted(transaction)
        );

        log.debug(
            "Processed deposit with transaction id {}",
            transaction.getId()
        );

        return DepositAccountResult.from(transaction);
    }
}