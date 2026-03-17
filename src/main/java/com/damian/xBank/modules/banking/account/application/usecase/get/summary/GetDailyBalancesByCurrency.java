package com.damian.xBank.modules.banking.account.application.usecase.get.summary;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Caso de uso que devuelve los balances de cada día asociados al balance de todas las cuentas
 * de la misma currency.
 */
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
     *
     * @param query La query con los datos que se necesitan
     * @return Los balances de cada día para una currency
     */
    @Transactional
    public GetDailyBalancesByCurrencyResult execute(GetDailyBalancesByCurrencyQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Balances por currency
        Set<Object> dailyBalancesSet = bankingAccountRepository
            .findDailyBalancesForUserAndCurrency(currentUser.getId(), query.currency());

        return new GetDailyBalancesByCurrencyResult(dailyBalancesSet);
    }
}