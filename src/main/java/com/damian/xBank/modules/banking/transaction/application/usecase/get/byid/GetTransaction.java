package com.damian.xBank.modules.banking.transaction.application.usecase.get.byid;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionDetailResult;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

/**
 * Caso de uso donde se consulta una transacción
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
     * @param query Consulta
     * @return Result con los datos de la transacción
     */
    public BankingTransactionDetailResult execute(GetTransactionQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // La transacción consultada
        final BankingTransaction transaction = bankingTransactionRepository
            .findById(query.transactionId())
            .orElseThrow(() -> new BankingTransactionNotFoundException(query.transactionId()));

        // Si no es admin ...
        if (!currentUser.isAdmin()) {
            transaction.assertOwnedBy(currentUser.getId());
        }

        return BankingTransactionDetailResult.from(transaction);
    }
}