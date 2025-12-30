package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingTransferConfirm {
    private final NotificationPublisher notificationPublisher;
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransferDomainService bankingTransferDomainService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingTransferConfirm(
            NotificationPublisher notificationPublisher,
            BankingAccountRepository bankingAccountRepository,
            BankingTransferDomainService bankingTransferDomainService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.notificationPublisher = notificationPublisher;
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransferDomainService = bankingTransferDomainService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public BankingTransfer confirmTransfer(
            Long transferId,
            BankingTransferConfirmRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // validate customer password
        passwordValidator.validatePassword(currentCustomer, request.password());

        BankingTransfer transfer = bankingTransferRepository.findById(transferId).orElseThrow(
                () -> new BankingTransferNotFoundException(transferId)
        );

        // confirm transfer
        bankingTransferDomainService.confirmTransfer(currentCustomer.getId(), transfer);

        // Save accounts (.save is optional because of transactional)
        // Saving the accounts also updates the transactions since we are using CASCADE.ALL
        bankingAccountRepository.save(transfer.getFromAccount());
        bankingAccountRepository.save(transfer.getToAccount());

        // Save transfer
        bankingTransferRepository.save(transfer);

        // Notify receive
        notificationPublisher.publish(
                new NotificationEvent(
                        transfer.getToAccount().getOwner().getAccount().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction",
                                BankingTransactionDtoMapper
                                        .toBankingTransactionDto(transfer.getToTransaction())
                        ),
                        Instant.now().toString()
                )
        );

        return transfer;
    }

}