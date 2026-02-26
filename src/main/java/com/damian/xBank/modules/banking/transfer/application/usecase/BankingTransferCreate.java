package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingTransferCreate {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingTransferCreate(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public BankingTransfer createTransfer(BankingTransferRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking account from where funds will be transferred.
        final BankingAccount fromAccount = bankingAccountRepository
            .findById(request.fromAccountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(request.fromAccountId())
            );

        // assert that the current user is the owner of the fromAccount
        fromAccount.assertOwnedBy(currentUser.getId());

        // Banking account to receive funds
        final BankingAccount toAccount = bankingAccountRepository
            .findByAccountNumber(request.toAccountNumber())
            .orElse(null);

        BankingTransfer transfer = BankingTransfer.create(
            currentUser.getId(),
            fromAccount,
            toAccount,
            request.amount(),
            request.description()
        );

        transfer.setToAccountIban(request.toAccountNumber());
        return bankingTransferRepository.save(transfer);
    }
}