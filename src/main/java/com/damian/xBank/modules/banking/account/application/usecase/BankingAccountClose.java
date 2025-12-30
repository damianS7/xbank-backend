package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountClose {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public BankingAccountClose(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.passwordValidator = passwordValidator;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Create a BankingAccount for the logged customer.
     *
     * @param request BankingAccountCreateRequest the request containing the data needed
     *                to create the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount execute(Long accountId, BankingAccountCloseRequest request) {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to be closed
        final BankingAccount bankingAccount = bankingAccountRepository.findById(accountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        accountId
                ) // Banking account not found
        );

        passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());

        // validations rules only for customers
        bankingAccount.closeBy(currentCustomer);

        return bankingAccountRepository.save(bankingAccount);
    }
}