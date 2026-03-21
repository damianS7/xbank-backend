package com.damian.xBank.modules.banking.transaction.application.usecase.get.pending;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para obtener todas las transacciones pendientes.
 */
@Service
public class GetPendingTransactions {
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public GetPendingTransactions(
        BankingTransactionRepository bankingTransactionRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * @param query Datos de consulta
     * @return Result con transacciones paginadas
     */
    public PageResult<BankingTransactionResult> execute(GetPendingTransactionsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        final Page<BankingTransaction> pagedTransactions = bankingTransactionRepository
            .findByStatusAndBankingAccount_User_Id(
                BankingTransactionStatus.PENDING,
                currentUser.getId(),
                query.pageable()
            );

        return PageResult.fromPagedTransactions(pagedTransactions);
    }
}