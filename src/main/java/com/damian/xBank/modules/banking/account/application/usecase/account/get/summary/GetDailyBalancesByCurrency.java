package com.damian.xBank.modules.banking.account.application.usecase.account.get.summary;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class GetDailyBalancesByCurrency {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public GetDailyBalancesByCurrency(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a set of data containing the total balance for every day
     * of every account with the same currency that the logged user posses.
     *
     * @return the updated banking account
     */
    @Transactional
    public GetDailyBalancesByCurrencyResult execute(GetDailyBalancesByCurrencyQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking accounts balances for the given currency
        Set<Object> dailyBalancesSet = bankingAccountRepository
            .findDailyBalancesForUserAndCurrency(currentUser.getId(), query.currency());

        return new GetDailyBalancesByCurrencyResult(dailyBalancesSet);
    }
}