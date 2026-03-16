package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCurrencyMismatchException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.repository.IncomingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeIncomingTransfer {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeIncomingTransfer.class);
    private final BankingAccountRepository bankingAccountRepository;
    private final IncomingTransferRepository incomingTransferRepository;

    public AuthorizeIncomingTransfer(
        BankingAccountRepository bankingAccountRepository,
        IncomingTransferRepository incomingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.incomingTransferRepository = incomingTransferRepository;
    }

    @Transactional
    public AuthorizeIncomingTransferResult execute(AuthorizeIncomingTransferCommand command) {
        log.debug("Authorize incoming transfer: {}", command);
        BankingAccount toAccount;
        try {
            toAccount = bankingAccountRepository
                .findByAccountNumber(command.toIban())
                .orElseThrow(
                    () -> new BankingAccountNotFoundException(command.toIban())
                );

            toAccount.assertActive();
            toAccount.assertCurrency(BankingAccountCurrency.valueOf(command.currency()));
        } catch (BankingAccountNotFoundException
                 | BankingAccountClosedException
                 | BankingAccountCurrencyMismatchException
                 | BankingAccountSuspendedException e) {
            log.warn("Failed to authorize incoming transfer: {}", e.getMessage());
            return new AuthorizeIncomingTransferResult(
                TransferAuthorizationStatus.REJECTED,
                command.authorizationId(),
                e.getMessage()
            );
        }

        IncomingTransfer incomingTransfer = IncomingTransfer.create(
            command.fromIban(),
            toAccount,
            toAccount.getAccountNumber(),
            command.amount(),
            "Incoming transfer from " + command.reference()
        );

        incomingTransfer.authorize(command.authorizationId());
        incomingTransferRepository.save(incomingTransfer);

        return new AuthorizeIncomingTransferResult(
            TransferAuthorizationStatus.AUTHORIZED,
            command.authorizationId(),
            null
        );
    }
}