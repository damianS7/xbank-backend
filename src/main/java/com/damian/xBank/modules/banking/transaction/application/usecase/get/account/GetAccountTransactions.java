package com.damian.xBank.modules.banking.transaction.application.usecase.get.account;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para obtener las transacciones de una cuenta
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
     * @param query Datos de la consulta
     * @return Result con las transacciones paginadas
     */
    public PageResult<BankingTransactionResult> execute(GetAccountTransactionsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        final BankingAccount account = bankingAccountRepository
            .findById(query.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(query.accountId())
            );

        // Si no es admin se comprueba que es el dueño
        if (currentUser.isAdmin()) {
            account.assertOwnedBy(currentUser.getId());
        }

        Page<BankingTransaction> pagedTransactions = bankingTransactionRepository
            .findByBankingAccount_Id(query.accountId(), query.pageable());

        return PageResult.fromPagedTransactions(pagedTransactions);
    }
}