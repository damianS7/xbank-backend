package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.cqrs.query.GetAllBankingAccountsQuery;
import com.damian.xBank.modules.banking.account.application.cqrs.result.BankingAccountResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetAllBankingAccounts {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public GetAllBankingAccounts(
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
    public Set<BankingAccountResult> execute(GetAllBankingAccountsQuery query) {
        // we extract the customer logged from the SecurityContext
        final User currentUser = authenticationContext.getCurrentUser();

        Set<BankingAccount> accounts = bankingAccountRepository.findByUser_Id(currentUser.getId());

        return accounts.stream()
            .map(BankingAccountResult::from)
            .collect(Collectors.toSet());
    }
}