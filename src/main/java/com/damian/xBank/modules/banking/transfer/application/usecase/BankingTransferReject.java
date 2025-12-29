package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRejectRequest;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.service.NotificationService;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingTransferReject {
    private final BankingTransferRepository transferRepository;
    private final NotificationService notificationService;
    private final BankingTransferService bankingTransferService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public BankingTransferReject(
            BankingTransferRepository transferRepository,
            NotificationService notificationService,
            BankingTransferService bankingTransferService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.transferRepository = transferRepository;
        this.notificationService = notificationService;
        this.bankingTransferService = bankingTransferService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    @Transactional
    public BankingTransfer rejectTransfer(
            Long transferId,
            BankingTransferRejectRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // validate customer password
        passwordValidator.validatePassword(currentCustomer, request.password());

        // find the transfer
        BankingTransfer transfer = transferRepository.findById(transferId).orElseThrow(
                () -> new BankingTransferNotFoundException(transferId)
        );

        // reject
        bankingTransferService.reject(currentCustomer.getId(), transfer);

        // Save
        transferRepository.save(transfer);

        // Notify receive
        notificationService.publish(
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