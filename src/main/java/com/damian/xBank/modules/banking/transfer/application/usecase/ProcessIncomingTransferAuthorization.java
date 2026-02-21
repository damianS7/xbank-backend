package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCurrencyMismatchException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.IncomingTransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.IncomingTransferAuthorizationResponse;
import org.springframework.stereotype.Service;

@Service
public class ProcessIncomingTransferAuthorization {
    private final BankingAccountRepository bankingAccountRepository;

    public ProcessIncomingTransferAuthorization(
        BankingAccountRepository bankingAccountRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
    }

    public IncomingTransferAuthorizationResponse execute(
        IncomingTransferAuthorizationRequest request
    ) {
        TransferAuthorizationStatus stauts = TransferAuthorizationStatus.REJECTED;
        String rejectionReason = null;

        try {
            BankingAccount account = bankingAccountRepository
                .findByAccountNumber(request.toIban())
                .orElseThrow(
                    () -> new BankingAccountNotFoundException(request.toIban())
                );

            account.assertActive();
            account.assertCurrency(BankingAccountCurrency.valueOf(request.currency()));

            stauts = TransferAuthorizationStatus.AUTHORIZED;
        } catch (BankingAccountNotFoundException
                 | BankingAccountClosedException
                 | BankingAccountCurrencyMismatchException
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