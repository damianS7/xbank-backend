package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankingTransactionGetPending {
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionGetPending(
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a paginated result containing the pending transactions from current customer.
     *
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> execute(Pageable pageable) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        return bankingTransactionRepository.findByStatusAndBankingAccount_User_Id(
                BankingTransactionStatus.PENDING,
                currentUser.getId(),
                pageable
        );
    }
}