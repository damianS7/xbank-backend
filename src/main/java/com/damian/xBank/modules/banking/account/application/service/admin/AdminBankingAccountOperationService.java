package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountGuard;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.notification.domain.enums.NotificationType;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.utils.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AdminBankingAccountOperationService {
    private static final Logger log = LoggerFactory.getLogger(AdminBankingAccountOperationService.class);
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;

    public AdminBankingAccountOperationService(
            BankingTransactionAccountService bankingTransactionAccountService,
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
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
        final Customer customer = AuthHelper.getCurrentCustomer();

        // The account to deposit into
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId).orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                bankingAccountId
                        ) // Banking account not found
                );

        // Validate account
        BankingAccountGuard.forAccount(bankingAccount)
                           .assertActive();

        BankingTransaction transaction = bankingTransactionAccountService.generateTransaction(
                bankingAccount,
                BankingTransactionType.DEPOSIT,
                request.amount(),
                "DEPOSIT by " + request.depositorName()
        );

        // if the transaction is created, add the amount to balance
        bankingAccount.addBalance(request.amount());

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        bankingTransactionAccountService.persistTransaction(transaction);

        // Notify receiver
        notificationService.publishNotification(
                new NotificationEvent(
                        bankingAccount.getOwner().getId(),
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