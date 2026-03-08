package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize;

import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.OutgoingTransferFailureRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HandleOutgoingTransferAuthorizationFailure {
    private final BankingTransferRepository bankingTransferRepository;

    public HandleOutgoingTransferAuthorizationFailure(
        BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public void execute(OutgoingTransferFailureRequest request) {
        BankingTransfer transfer = bankingTransferRepository
            .findByProviderAuthorizationId(
                request.authorizationId()
            ).orElseThrow(
                () -> new BankingTransferNotFoundException(request.authorizationId())
            );

        transfer.reject(request.failure());

        // revert money
        transfer.getFromAccount().addBalance(transfer.getAmount());
    }
}