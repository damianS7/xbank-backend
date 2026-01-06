package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BankingAccountGetAll {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public BankingAccountGetAll(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Get a set of BankingAccount with all the accounts from the logged customer
     *
     * @return a Set with all the BankingAccounts from the logged customer
     */
    public Set<BankingAccount> execute() {
        // we extract the customer logged from the SecurityContext
        final User currentUser = authenticationContext.getCurrentUser();

        return bankingAccountRepository.findByUser_Id(currentUser.getId());
    }
}