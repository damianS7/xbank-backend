package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.service.NotificationService;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingTransferCreate {
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;
    private final BankingTransferService bankingTransferService;
    private final AuthenticationContext authenticationContext;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingTransferCreate(
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService,
            BankingTransferService bankingTransferService,
            AuthenticationContext authenticationContext,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
        this.bankingTransferService = bankingTransferService;
        this.authenticationContext = authenticationContext;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public BankingTransfer createTransfer(BankingTransferRequest request) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account from where funds will be transfered.
        final BankingAccount fromAccount = bankingAccountRepository
                .findById(request.fromAccountId())
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(request.fromAccountId())
                );

        // Banking account to receive funds
        final BankingAccount toAccount = bankingAccountRepository
                .findByAccountNumber(request.toAccountNumber())
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(request.toAccountNumber())
                );

        BankingTransfer transfer = bankingTransferService.createTransfer(
                currentCustomer.getId(),
                fromAccount,
                toAccount,
                request.amount(),
                request.description()
        );

        // Notify fromAccount
        notificationService.publish(
                new NotificationEvent(
                        toAccount.getOwner().getAccount().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction",
                                BankingTransactionDtoMapper
                                        .toBankingTransactionDto(transfer.getToTransaction())
                        ),
                        Instant.now().toString()
                )
        );

        return bankingTransferRepository.save(transfer);
    }

}