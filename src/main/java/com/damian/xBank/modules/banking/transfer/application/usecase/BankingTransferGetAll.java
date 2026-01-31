package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankingTransferGetAll {
    private final AuthenticationContext authenticationContext;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingTransferGetAll(
            AuthenticationContext authenticationContext,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    public Page<BankingTransfer> execute(Pageable pageable) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        return bankingTransferRepository
                .findAllByFromAccount_UserId(currentUser.getId(), pageable);
    }

}