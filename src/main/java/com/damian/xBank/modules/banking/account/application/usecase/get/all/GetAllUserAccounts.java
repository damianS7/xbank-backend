package com.damian.xBank.modules.banking.account.application.usecase.get.all;

import com.damian.xBank.modules.banking.account.application.dto.BankingAccountResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetAllUserAccounts {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public GetAllUserAccounts(
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
    public GetAllUserAccountsResult execute(GetAllUserAccountsQuery query) {
        // we extract the customer logged from the SecurityContext
        final User currentUser = authenticationContext.getCurrentUser();

        Set<BankingAccount> accounts = bankingAccountRepository.findByUser_Id(currentUser.getId());

        Set<BankingAccountResult> accountsResultSet = accounts.stream()
            .map(BankingAccountResult::from)
            .collect(Collectors.toSet());

        return new GetAllUserAccountsResult(accountsResultSet);
    }
}