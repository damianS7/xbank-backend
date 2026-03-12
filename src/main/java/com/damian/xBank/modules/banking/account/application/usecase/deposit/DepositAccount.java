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
     * Deposit into banking account
     *
     * @param command
     * @return BankingTransaction
     */
    @Transactional
    public DepositAccountResult execute(DepositAccountCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // if the logged customer is not admin or bank manager
        if (!currentUser.isAdmin()) {
            throw new BankingAccountDepositNotAdminException(
                command.bankingAccountId(), currentUser.getId()
            );
        }

        // The account to deposit into
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.bankingAccountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(
                    command.bankingAccountId()
                ) // Banking account not found
            );

        // Validate account is operable
        bankingAccount.assertActive();

        // if the transaction is created, add the amount to balance
        BankingTransaction transaction = BankingTransaction.create(
            BankingTransactionType.DEPOSIT,
            bankingAccount,
            command.amount(),
            "DEPOSIT by " + command.depositorName()
        );

        bankingAccount.deposit(command.amount());
        transaction.complete();

        // save the transaction
        bankingTransactionRepository.save(transaction);

        // Notify receiver
        notificationPublisher.publish(
            notificationEventFactory.depositCompleted(transaction)
        );

        log.debug(
            "Admin {} processed deposit with transaction id {}",
            currentUser.getId(),
            transaction.getId()
        );

        return DepositAccountResult.from(transaction);
    }
}