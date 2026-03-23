package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para crear transferencias salientes
 */
@Service
public class CreateOutgoingTransfer {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final OutgoingTransferRepository outgoingTransferRepository;

    public CreateOutgoingTransfer(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        OutgoingTransferRepository outgoingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.outgoingTransferRepository = outgoingTransferRepository;
    }

    @Transactional
    public CreateOutgoingTransferResult execute(CreateOutgoingTransferCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Cuenta desde donde se envían los fondos
        final BankingAccount fromAccount = bankingAccountRepository
            .findById(command.fromAccountId())
            .orElseThrow(() -> new BankingAccountNotFoundException(command.fromAccountId()));

        // assert that the current user is the owner of the fromAccount
        fromAccount.assertOwnedBy(currentUser.getId());

        // Cuenta hacia donde se envían los fondos
        final BankingAccount toAccount = bankingAccountRepository
            .findByAccountNumber(command.toAccountNumber())
            .orElse(null);

        OutgoingTransfer transfer = OutgoingTransfer.create(
            fromAccount,
            toAccount,
            command.toAccountNumber(),
            command.amount(),
            command.description()
        );

        outgoingTransferRepository.save(transfer);

        return CreateOutgoingTransferResult.from(transfer);
    }
}