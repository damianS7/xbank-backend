package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingInternalTransfer {
    private final OutgoingTransferRepository outgoingTransferRepository;

    public AuthorizeOutgoingInternalTransfer(
        OutgoingTransferRepository outgoingTransferRepository
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
    }

    @Transactional
    public void execute(AuthorizeOutgoingTransferCommand command) {
        OutgoingTransfer transfer = outgoingTransferRepository
            .findById(command.transferId())
            .orElseThrow();

        transfer.authorize("provider-id");

        outgoingTransferRepository.save(transfer);
    }
}