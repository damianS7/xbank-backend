package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountSetAliasRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BankingAccountSetAlias {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public BankingAccountSetAlias(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Set an alias for a banking account
     *
     * @param request BankingAccountAliasUpdateRequest the request containing the new alias
     * @return the updated BankingAccount
     */
    public BankingAccount execute(
            Long accountId,
            BankingAccountSetAliasRequest request
    ) {
        // Customer logged
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking account to set alias
        final BankingAccount bankingAccount = bankingAccountRepository.findById(accountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        accountId
                ) // Banking account not found
        );

        // validations rules only for customers
        if (!currentUser.isAdmin()) {

            bankingAccount.assertOwnedBy(currentUser.getId())
                          .assertActive();
        }

        // we mark the account as closed
        bankingAccount.setAlias(request.alias());

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingAccountRepository.save(bankingAccount);
    }
}