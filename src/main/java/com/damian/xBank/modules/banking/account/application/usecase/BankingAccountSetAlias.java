package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
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
     * Create a BankingAccount for the logged customer.
     *
     * @param request BankingAccountCreateRequest the request containing the data needed
     *                to create the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount execute(
            Long accountId,
            BankingAccountAliasUpdateRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to set alias
        final BankingAccount bankingAccount = bankingAccountRepository.findById(accountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        accountId
                ) // Banking account not found
        );

        // validations rules only for customers
        if (!currentCustomer.isAdmin()) {

            bankingAccount.assertOwnedBy(currentCustomer.getId())
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