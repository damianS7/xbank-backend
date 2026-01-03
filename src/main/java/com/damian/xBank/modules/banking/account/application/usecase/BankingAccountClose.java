package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.model.User;
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
     * Closes a BankingAccount from the logged customer.
     *
     * @param request BankingAccountCloseRequest the request
     * @return the banking account with the CLOSED status.
     */
    public BankingAccount execute(Long accountId, BankingAccountCloseRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking account to be closed
        final BankingAccount bankingAccount = bankingAccountRepository.findById(accountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        accountId
                ) // Banking account not found
        );

        passwordValidator.validatePassword(currentUser, request.password());

        // validations rules only for customers
        bankingAccount.closeBy(currentUser);

        return bankingAccountRepository.save(bankingAccount);
    }
}