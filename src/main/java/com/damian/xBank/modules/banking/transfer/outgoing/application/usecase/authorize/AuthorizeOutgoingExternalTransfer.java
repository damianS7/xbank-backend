package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize;

import com.damian.xBank.modules.banking.transfer.outgoing.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.event.OutgoingTransferSentEvent;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferAuthorizationFailedException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingExternalTransfer {
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final TransferAuthorizationGateway transferAuthorizationGateway;
    private final ApplicationEventPublisher eventPublisher;

    public AuthorizeOutgoingExternalTransfer(
        OutgoingTransferRepository outgoingTransferRepository,
        TransferAuthorizationGateway transferAuthorizationGateway,
        ApplicationEventPublisher eventPublisher
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.transferAuthorizationGateway = transferAuthorizationGateway;
        this.eventPublisher = eventPublisher;
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
        outgoingTransferRepository.save(transfer);

        // Notify sender
        eventPublisher.publishEvent(
            new OutgoingTransferSentEvent(
                transfer.getId(),
                transfer.getFromAccount().getOwner().getId(),
                transfer.getFromTransaction().getId(),
                transfer.getFromAccount().getOwner().getProfile().getFullName(),
                transfer.getAmount(),
                transfer.getFromAccount().getCurrency().toString()
            )
        );
    }
}