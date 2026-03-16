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
 * TODO Merge with AuthorizeIncomingTrnasfer???
 * Use case for processing an incoming transfer. This involves:
 * 1. Validating the incoming transfer request.
 * 2. Updating the recipient's account balance.
 * 3. Recording the transaction in the banking transaction history.
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
        // find transfer by authorizationId
        IncomingTransfer incomingTransfer = incomingTransferRepository
            .findByProviderAuthorizationId(command.authorizationId())
            .orElseThrow(
                () -> new IncomingTransferNotFoundException(command.authorizationId())
            );

        incomingTransfer.complete(); // Deposit from here?
        incomingTransferRepository.save(incomingTransfer);
    }
}