package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingTransferCreate {
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationPublisher notificationPublisher;
    private final BankingTransferDomainService bankingTransferDomainService;
    private final AuthenticationContext authenticationContext;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingTransferCreate(
            BankingAccountRepository bankingAccountRepository,
            NotificationPublisher notificationPublisher,
            BankingTransferDomainService bankingTransferDomainService,
            AuthenticationContext authenticationContext,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationPublisher = notificationPublisher;
        this.bankingTransferDomainService = bankingTransferDomainService;
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

        BankingTransfer transfer = bankingTransferDomainService.createTransfer(
                currentCustomer.getId(),
                fromAccount,
                toAccount,
                request.amount(),
                request.description()
        );

        // Notify fromAccount
        notificationPublisher.publish(
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