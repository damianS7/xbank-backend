package com.damian.xBank.modules.payment.network.transfer.application.usecase;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionFactory;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.InitiateOutgoingTransferRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.TransferNetworkAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.infrastructure.web.TransferNetworkHttpGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitiateOutgoingTransfer {
    private final BankingTransferRepository bankingTransferRepository;
    private final TransferNetworkHttpGateway transferNetworkHttpGateway;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final BankingTransactionFactory bankingTransactionFactory;

    public InitiateOutgoingTransfer(
        BankingTransferRepository bankingTransferRepository,
        TransferNetworkHttpGateway transferNetworkHttpGateway,
        BankingTransactionRepository bankingTransactionRepository,
        BankingTransactionFactory bankingTransactionFactory
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.transferNetworkHttpGateway = transferNetworkHttpGateway;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.bankingTransactionFactory = bankingTransactionFactory;
    }

    @Transactional
    public void execute(InitiateOutgoingTransferRequest request) {
        BankingTransfer transfer = bankingTransferRepository
            .findById(request.transferId())
            .orElseThrow();

        transferNetworkHttpGateway.authorizeTransfer(
            new TransferNetworkAuthorizationRequest(
                transfer.getFromAccount().getAccountNumber(),
                transfer.getToAccount().getAccountNumber(),
                transfer.getAmount(),
                transfer.getFromAccount().getCurrency().toString(),
                transfer.getDescription()
            )
        );
    }
}