package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize;

import com.damian.xBank.modules.banking.transfer.outgoing.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferAuthorizationFailedException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingExternalTransfer {
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final TransferAuthorizationGateway transferAuthorizationGateway;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;

    public AuthorizeOutgoingExternalTransfer(
        OutgoingTransferRepository outgoingTransferRepository,
        TransferAuthorizationGateway transferAuthorizationGateway,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.transferAuthorizationGateway = transferAuthorizationGateway;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public void execute(AuthorizeOutgoingTransferCommand command) {
        OutgoingTransfer transfer = outgoingTransferRepository
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
            throw new OutgoingTransferAuthorizationFailedException(
                transfer.getId(),
                response.rejectionReason()
            );
        }

        transfer.authorize(response.authorizationId());

        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferAuthorized(transfer)
        );

        outgoingTransferRepository.save(transfer);
    }
}