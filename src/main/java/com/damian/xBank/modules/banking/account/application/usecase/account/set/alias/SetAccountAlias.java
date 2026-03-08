package com.damian.xBank.modules.banking.account.application.usecase.account.set.alias;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SetAccountAlias {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public SetAccountAlias(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Set an alias for a banking account
     *
     * @param command BankingAccountAliasUpdateRequest the command containing the new alias
     * @return the updated BankingAccount
     */
    public SetAccountAliasResult execute(SetAccountAliasCommand command) {
        // Customer logged
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking account to set alias
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(
                    command.accountId()
                ) // Banking account not found
            );

        // validations rules only for customers
        if (!currentUser.isAdmin()) {

            bankingAccount.assertOwnedBy(currentUser.getId())
                .assertActive();
        }

        // we mark the account as closed
        bankingAccount.setAlias(command.alias());

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        bankingAccountRepository.save(bankingAccount);

        return SetAccountAliasResult.from(bankingAccount);
    }
}