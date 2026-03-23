package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.complete;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.event.OutgoingTransferCompletedEvent;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para completar transferencias. Es el paso final, aquí cambiamos el estado a completada
 * y notificamos a los destinatarios.
 */
@Service
public class CompleteOutgoingInternalTransfer {
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CompleteOutgoingInternalTransfer(
        OutgoingTransferRepository outgoingTransferRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(OutgoingTransfer transfer) {
        transfer.complete();
        outgoingTransferRepository.save(transfer);

        eventPublisher.publishEvent(
            new OutgoingTransferCompletedEvent(
                transfer.getId(),
                transfer.getType(),
                transfer.getFromAccount().getOwner().getId(),
                transfer.getFromAccount().getOwner().getProfile().getFullName(),
                transfer.getFromTransaction().getId(),
                transfer.getToAccount().getOwner().getId(),
                transfer.getToAccount().getOwner().getProfile().getFullName(),
                transfer.getToTransaction().getId(),
                transfer.getAmount(),
                transfer.getFromAccount().getCurrency().toString()
            )
        );
    }
}