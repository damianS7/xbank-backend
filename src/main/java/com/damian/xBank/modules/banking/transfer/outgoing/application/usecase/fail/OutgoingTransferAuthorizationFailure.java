package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde un servicio externo (TransferNetwork) reporta que la transferencia
 * iniciada por este banco ha fallado.
 */
@Service
public class OutgoingTransferAuthorizationFailure {
    private final OutgoingTransferRepository outgoingTransferRepository;

    public OutgoingTransferAuthorizationFailure(
        OutgoingTransferRepository outgoingTransferRepository
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
    }

    @Transactional
    public FailedOutgoingTransferResult execute(OutgoingTransferFailureRequest request) {
        OutgoingTransfer transfer = outgoingTransferRepository
            .findByProviderAuthorizationId(request.authorizationId())
            .orElseThrow(() -> new OutgoingTransferNotFoundException(request.authorizationId()));

        // Falla la transferencia
        transfer.fail(request.failure());

        return FailedOutgoingTransferResult.from(transfer);
    }
}