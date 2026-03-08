package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize;

import com.damian.xBank.modules.banking.transfer.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferAuthorizationFailedException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.response.TransferAuthorizationResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingExternalTransfer {
    private final BankingTransferRepository bankingTransferRepository;
    private final TransferAuthorizationGateway transferAuthorizationGateway;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;

    public AuthorizeOutgoingExternalTransfer(
        BankingTransferRepository bankingTransferRepository,
        TransferAuthorizationGateway transferAuthorizationGateway,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.transferAuthorizationGateway = transferAuthorizationGateway;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public void execute(AuthorizeOutgoingTransferCommand command) {
        BankingTransfer transfer = bankingTransferRepository
            .findById(command.transferId())
            .orElseThrow();

        TransferAuthorizationResponse response = transferAuthorizationGateway.authorizeTransfer(
            new TransferAuthorizationRequest(
                transfer.getFromAccount().getAccountNumber(),
                transfer.getToAccountIban(),
                transfer.getAmount(),
                transfer.getFromAccount().getCurrency().toString(),
                transfer.getDescription()
            )
        );

        if (response.status() != TransferAuthorizationStatus.PENDING) {
            throw new BankingTransferAuthorizationFailedException(
                transfer.getId(),
                response.rejectionReason()
            );
        }

        transfer.setProviderAuthorizationId(response.authorizationId());

        transfer.authorize();

        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferAuthorized(transfer)
        );

        bankingTransferRepository.save(transfer);
    }
}