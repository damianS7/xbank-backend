package com.damian.xBank.modules.banking.transaction.application.usecase.get.account;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Use case for retrieving the transactions of a banking account.
 */
@Service
public class GetAccountTransactions {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public GetAccountTransactions(
        BankingAccountRepository bankingAccountRepository,
        BankingTransactionRepository bankingTransactionRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @param query
     * @return a result containing the paged transactions.
     */
    public GetAccountTransactionsResult execute(GetAccountTransactionsQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingAccount account = bankingAccountRepository
            .findById(query.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(query.accountId())
            );

        // if the current user is a customer ...
        if (currentUser.hasRole(UserRole.CUSTOMER)) {

            // assert account belongs to him
            account.assertOwnedBy(currentUser.getId());
        }

        Page<BankingTransaction> pagedTransactions = bankingTransactionRepository
            .findByBankingAccountId(query.accountId(), query.pageable());

        return new GetAccountTransactionsResult(
            BankingTransactionDtoMapper.toBankingTransactionPagedResult(pagedTransactions)
        );
    }
}