package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionFactory;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.controller.TransferAuthorizationNetworkHttpGateway;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.InitiateOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferAuthorizationNetworkRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferAuthorizationNetworkResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitiateOutgoingTransferAuthorization {
    private final BankingTransferRepository bankingTransferRepository;
    private final TransferAuthorizationNetworkHttpGateway transferAuthorizationNetworkHttpGateway;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final BankingTransactionFactory bankingTransactionFactory;

    public InitiateOutgoingTransferAuthorization(
        BankingTransferRepository bankingTransferRepository,
        TransferAuthorizationNetworkHttpGateway transferAuthorizationNetworkHttpGateway,
        BankingTransactionRepository bankingTransactionRepository,
        BankingTransactionFactory bankingTransactionFactory
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.transferAuthorizationNetworkHttpGateway = transferAuthorizationNetworkHttpGateway;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.bankingTransactionFactory = bankingTransactionFactory;
    }

    @Transactional
    public void execute(InitiateOutgoingTransferRequest request) {
        BankingTransfer transfer = bankingTransferRepository
            .findById(request.transferId())
            .orElseThrow();

        TransferAuthorizationNetworkResponse response = transferAuthorizationNetworkHttpGateway.authorizeTransfer(
            new TransferAuthorizationNetworkRequest(
                transfer.getFromAccount().getAccountNumber(),
                transfer.getToAccount().getAccountNumber(),
                transfer.getAmount(),
                transfer.getFromAccount().getCurrency().toString(),
                transfer.getDescription()
            )
        );

        transfer.setProviderAuthorizationId(response.authorizationId());
    }
}