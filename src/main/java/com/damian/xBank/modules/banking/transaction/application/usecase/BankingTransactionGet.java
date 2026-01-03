package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.domain.model.UserAccountRole;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

@Service
public class BankingTransactionGet {
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionGet(
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a single transaction by id.
     *
     * @param transactionId
     * @return requested transaction
     */
    public BankingTransaction execute(Long transactionId) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingTransaction transaction = bankingTransactionRepository
                .findById(transactionId)
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(transactionId)
                );

        // if the current user is a customer ...
        if (currentUser.hasRole(UserAccountRole.CUSTOMER)) {

            // assert transaction belongs to him
            transaction.assertOwnedBy(currentUser.getId());

        }

        return transaction;
    }
}