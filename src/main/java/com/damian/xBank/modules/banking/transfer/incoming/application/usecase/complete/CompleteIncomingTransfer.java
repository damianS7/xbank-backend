package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.incoming.domain.exception.IncomingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.repository.IncomingTransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso que completa una transferencia entrante.
 * TODO Merge with AuthorizeIncomingTrnasfer???
 */
@Service
public class CompleteIncomingTransfer {
    private static final Logger log = LoggerFactory.getLogger(CompleteIncomingTransfer.class);
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final IncomingTransferRepository incomingTransferRepository;

    public CompleteIncomingTransfer(
        BankingAccountRepository bankingAccountRepository,
        BankingTransactionRepository bankingTransactionRepository,
        IncomingTransferRepository incomingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.incomingTransferRepository = incomingTransferRepository;
    }

    @Transactional
    public void execute(CompleteIncomingTransferCommand command) {
        log.debug("Complete transfer command: {}", command);
        // Buscar la transferencia
        IncomingTransfer incomingTransfer = incomingTransferRepository
            .findByProviderAuthorizationId(command.authorizationId())
            .orElseThrow(() -> new IncomingTransferNotFoundException(command.authorizationId()));
        incomingTransfer.complete();
        incomingTransferRepository.save(incomingTransfer);
    }
}