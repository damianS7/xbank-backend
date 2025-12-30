package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountActivation {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public BankingAccountActivation(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.passwordValidator = passwordValidator;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Change the status of a banking account to ACTIVE
     *
     * @param accountId the id of the banking account to activate
     * @return the updated banking account
     */
    public BankingAccount execute(Long accountId, BankingAccountOpenRequest request) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to activate
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(accountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                accountId
                        ) // Banking account not found
                );

        // validations rules only for customers
        bankingAccount.activateBy(currentCustomer);

        return bankingAccountRepository.save(bankingAccount);
    }
}