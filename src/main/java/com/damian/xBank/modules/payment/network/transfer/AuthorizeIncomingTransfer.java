package com.damian.xBank.modules.payment.network.transfer;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.IncomingTransferAuthorizationResponse;
import com.damian.xBank.modules.payment.network.transfer.domain.IncomingTransferAuthorizationStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeIncomingTransfer {
    private final BankingAccountRepository bankingAccountRepository;

    public AuthorizeIncomingTransfer(
        BankingAccountRepository bankingAccountRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
    }

    public IncomingTransferAuthorizationResponse execute(
        IncomingTransferAuthorizationRequest request
    ) {
        IncomingTransferAuthorizationStatus stauts = IncomingTransferAuthorizationStatus.REJECTED;
        String rejectionReason = null;

        try {
            BankingAccount account = bankingAccountRepository
                .findByAccountNumber(request.toIban())
                .orElseThrow(
                    () -> new BankingAccountNotFoundException(request.toIban())
                );

            account.assertActive();
            // account.assertCurrency

            stauts = IncomingTransferAuthorizationStatus.AUTHORIZED;
        } catch (BankingAccountNotFoundException
                 | BankingAccountClosedException
                 | BankingAccountSuspendedException e) {
            rejectionReason = e.getMessage();
        }

        return new IncomingTransferAuthorizationResponse(
            stauts,
            null, //            request.authorizationId(),
            rejectionReason
        );
    }
}