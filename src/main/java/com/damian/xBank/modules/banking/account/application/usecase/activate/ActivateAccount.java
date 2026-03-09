package com.damian.xBank.modules.banking.account.application.usecase.activate;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateAccount {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public ActivateAccount(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Change the status of a banking account to ACTIVE
     *
     * @param command the id of the banking account to activate
     * @return the updated banking account
     */
    @Transactional
    public ActivateAccountResult execute(ActivateAccountCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking account to activate
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(
                    command.accountId()
                ) // Banking account not found
            );

        // validations rules only for customers
        bankingAccount.activateBy(currentUser);

        bankingAccountRepository.save(bankingAccount);

        return ActivateAccountResult.from(bankingAccount);
    }
}