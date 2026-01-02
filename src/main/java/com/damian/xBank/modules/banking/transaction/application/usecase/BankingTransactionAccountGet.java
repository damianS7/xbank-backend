package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankingTransactionAccountGet {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionAccountGet(
            BankingAccountRepository bankingAccountRepository,
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a paginated result containing the transactions from a banking account.
     *
     * @param accountId the id of the banking account
     * @param pageable
     * @return a paginated result containing the transactions from a banking account.
     */
    public Page<BankingTransaction> execute(Long accountId, Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingAccount account = bankingAccountRepository
                .findById(accountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(accountId)
                );

        // if the current user is a customer ...
        if (currentCustomer.hasRole(UserAccountRole.CUSTOMER)) {

            // assert account belongs to him
            account.assertOwnedBy(currentCustomer.getId());
        }

        return bankingTransactionRepository.findByBankingAccountId(accountId, pageable);
    }
}