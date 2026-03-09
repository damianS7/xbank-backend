package com.damian.xBank.modules.banking.transaction.application.usecase.get.pending;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Use case for retrieving the pending transactions of the current customer.
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
     *
     * @param query the query containing the pagination information
     * @return a paginated result containing the pending transactions from current customer
     */
    public GetPendingTransactionsResult execute(GetPendingTransactionsQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        Page<BankingTransaction> pagedTransactions = bankingTransactionRepository
            .findByStatusAndBankingAccount_User_Id(
                BankingTransactionStatus.PENDING,
                currentUser.getId(),
                query.pageable()
            );

        return new GetPendingTransactionsResult(
            BankingTransactionDtoMapper.toBankingTransactionPagedResult(pagedTransactions)
        );
    }
}