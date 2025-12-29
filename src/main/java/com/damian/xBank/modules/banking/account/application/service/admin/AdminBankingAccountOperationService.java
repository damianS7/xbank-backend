package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AdminBankingAccountOperationService {
    private static final Logger log = LoggerFactory.getLogger(AdminBankingAccountOperationService.class);
    private final BankingTransactionService bankingTransactionService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthenticationContext authenticationContext;

    public AdminBankingAccountOperationService(
            BankingTransactionService bankingTransactionService,
            BankingAccountRepository bankingAccountRepository,
            NotificationPublisher notificationPublisher,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionService = bankingTransactionService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationPublisher = notificationPublisher;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Deposit into banking account
     *
     * @param bankingAccountId
     * @param request
     * @return BankingTransaction
     */
    public BankingTransaction deposit(
            Long bankingAccountId,
            BankingAccountDepositRequest request
    ) {
        final Customer customer = authenticationContext.getCurrentCustomer();

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
        bankingTransactionService.record(transaction);

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