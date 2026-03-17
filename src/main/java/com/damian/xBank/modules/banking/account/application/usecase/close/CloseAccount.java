package com.damian.xBank.modules.banking.account.application.usecase.close;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.AuthorizationException;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para cerrar una cuenta bancaria.
 */
@Service
public class CloseAccount {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public CloseAccount(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator
    ) {
        this.passwordValidator = passwordValidator;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * @param command Comando con los datos requeridos
     * @return La cuenta cerrada
     */
    public CloseAccountResult execute(CloseAccountCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // only admin can close accounts
        if (!currentUser.isAdmin()) {
            throw new AuthorizationException();
        }

        // Banking account to be closed
        final BankingAccount bankingAccount = bankingAccountRepository.findById(command.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(
                    command.accountId()
                ) // Banking account not found
            );

        bankingAccount.close();
        bankingAccountRepository.save(bankingAccount);

        return CloseAccountResult.from(bankingAccount);
    }
}