package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.transaction.application.cqrs.query.GetTransactionQuery;
import com.damian.xBank.modules.banking.transaction.application.cqrs.result.BankingTransactionDetailResult;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

/**
 * Use case for retrieving a single transaction by id.
 */
@Service
public class GetTransaction {
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public GetTransaction(
        BankingTransactionRepository bankingTransactionRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a single transaction by id.
     *
     * @param query
     * @return requested transaction
     */
    public BankingTransactionDetailResult execute(GetTransactionQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingTransaction transaction = bankingTransactionRepository
            .findById(query.transactionId())
            .orElseThrow(
                () -> new BankingTransactionNotFoundException(query.transactionId())
            );

        // if the current user is a customer ...
        if (currentUser.hasRole(UserRole.CUSTOMER)) {

            // assert transaction belongs to him
            transaction.assertOwnedBy(currentUser.getId());
        }

        return BankingTransactionDetailResult.from(transaction);
    }
}