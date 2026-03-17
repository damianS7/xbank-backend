package com.damian.xBank.modules.banking.account.application.usecase.get.all;

import com.damian.xBank.modules.banking.account.application.dto.BankingAccountResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso que devuelve todas las cuentas asociadas al usuario actual
 */
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
     * @return Todas las cuentas asociadas al usuario actual
     */
    public GetAllUserAccountsResult execute(GetAllUserAccountsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        Set<BankingAccount> accounts = bankingAccountRepository.findByUser_Id(currentUser.getId());
        Set<BankingAccountResult> accountsResultSet = accounts.stream()
            .map(BankingAccountResult::from)
            .collect(Collectors.toSet());

        return new GetAllUserAccountsResult(accountsResultSet);
    }
}