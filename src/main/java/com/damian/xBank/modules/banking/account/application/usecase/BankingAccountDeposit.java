package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingAccountDeposit {
    private static final Logger log = LoggerFactory.getLogger(BankingAccountDeposit.class);
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;
    private final NotificationPublisher notificationPublisher;
    private final PasswordValidator passwordValidator;

    public BankingAccountDeposit(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext,
            BankingTransactionPersistenceService bankingTransactionPersistenceService,
            NotificationPublisher notificationPublisher,
            PasswordValidator passwordValidator
    ) {
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.notificationPublisher = notificationPublisher;
        this.passwordValidator = passwordValidator;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Deposit into banking account
     *
     * @param bankingAccountId
     * @param request
     * @return BankingTransaction
     */
    public BankingTransaction execute(
            Long bankingAccountId,
            BankingAccountDepositRequest request
    ) {
        final Customer customer = authenticationContext.getCurrentCustomer();

        // TODO CHECK ADMIN HERE???
        // The account to deposit into
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId).orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                bankingAccountId
                        ) // Banking account not found
                );

        // Validate account is operable
        bankingAccount.assertActive();

        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.DEPOSIT,
                        bankingAccount,
                        request.amount()
                )
                .setDescription("DEPOSIT by " + request.depositorName());

        // if the transaction is created, add the amount to balance
        bankingAccount.addBalance(request.amount());

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        bankingTransactionPersistenceService.record(transaction);

        // Notify receiver
        notificationPublisher.publish(
                new NotificationEvent(
                        bankingAccount.getOwner().getAccount().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction", BankingTransactionDtoMapper.toBankingTransactionDto(transaction)
                        ),
                        Instant.now().toString()
                )
        );

        log.debug(
                "Admin {} processed deposit with transaction id {}",
                customer.getId(),
                transaction.getId()
        );

        return transaction;
    }
}