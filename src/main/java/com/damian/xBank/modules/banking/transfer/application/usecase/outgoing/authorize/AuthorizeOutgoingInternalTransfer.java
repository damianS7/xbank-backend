package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingInternalTransfer {
    private final BankingTransferRepository bankingTransferRepository;

    public AuthorizeOutgoingInternalTransfer(
        BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public void execute(AuthorizeOutgoingTransferCommand command) {
        BankingTransfer transfer = bankingTransferRepository
            .findById(command.transferId())
            .orElseThrow();

        transfer.authorize("provider-id");

        bankingTransferRepository.save(transfer);
    }
}