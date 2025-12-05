package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountGuard;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountOperationGuard;
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
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class BankingAccountOperationService {
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;

    public BankingAccountOperationService(
            BankingTransactionAccountService bankingTransactionAccountService,
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
    }

    /**
     * Transfer funds from one banking account to another.
     *
     * @param fromBankingAccountId ID of the banking account to transfer funds from
     * @param request              Transfer request containing the details
     * @return the created BankingTransaction
     */
    public BankingTransaction transfer(
            Long fromBankingAccountId,
            BankingAccountTransferRequest request
    ) {

        // Banking account to receive funds
        final BankingAccount fromBankingAccount = bankingAccountRepository
                .findById(fromBankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.BANKING.ACCOUNT.NOT_FOUND, fromBankingAccountId
                        )
                );

        return this.transferTo(
                fromBankingAccount,
                request.toBankingAccountNumber(),
                request.password(),
                request.amount(),
                request.description()
        );
    }

    // validates account status and does the transaction
    public BankingTransaction transferTo(
            BankingAccount fromBankingAccount,
            String toBankingAccountNumber,
            String password,
            BigDecimal amount,
            String description
    ) {
        // Banking account to receive funds
        final BankingAccount toBankingAccount = bankingAccountRepository
                .findByAccountNumber(toBankingAccountNumber)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.BANKING.ACCOUNT.NOT_FOUND, toBankingAccountNumber
                        )
                );

        final Customer customer = AuthHelper.getCurrentCustomer();

        AuthHelper.validatePassword(customer, password);

        // run validations and throw if any throw exception
        BankingAccountGuard
                .forAccount(fromBankingAccount)
                .ownership(customer);

        BankingAccountOperationGuard
                .forAccount(fromBankingAccount)
                .validateTransfer(toBankingAccount, amount);

        return this.transferTo(fromBankingAccount, toBankingAccount, amount, description);
    }

    public BankingTransaction transferTo(
            BankingAccount fromBankingAccount,
            BankingAccount toBankingAccount,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction fromTransaction = this.bankingTransactionAccountService.createTransaction(
                fromBankingAccount,
                BankingTransactionType.TRANSFER_TO,
                amount,
                description
        );

        fromBankingAccount.subtractBalance(amount);
        fromTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionAccountService.persistTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = this.bankingTransactionAccountService.createTransaction(
                toBankingAccount,
                BankingTransactionType.TRANSFER_FROM,
                amount,
                "Transfer from " + fromBankingAccount.getOwner().getFullName()
        );

        toBankingAccount.addBalance(amount);
        toTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionAccountService.persistTransaction(toTransaction);

        // Notify receiver
        notificationService.publishNotification(
                new NotificationEvent(
                        toBankingAccount.getOwner().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction", BankingTransactionDtoMapper.toBankingTransactionDto(toTransaction)
                        ),
                        Instant.now().toString()
                )
        );

        return fromTransaction;
    }
}