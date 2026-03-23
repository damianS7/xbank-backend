package com.damian.xBank.modules.banking.account.application.usecase.activate;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.AuthorizationException;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para activar cuentas bancarias.
 */
@Service
public class ActivateAccount {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public ActivateAccount(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @param command El comando con los datos necesarios para activar la cuenta.
     * @return La cuenta activada.
     */
    @Transactional
    public ActivateAccountResult execute(ActivateAccountCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Solo un administrador debe poder activar una cuenta.
        if (!currentUser.isAdmin()) {
            throw new AuthorizationException();
        }

        // Buscar la cuenta que hay que activar
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(command.accountId())
            );

        // Activar la cuenta
        bankingAccount.activate();
        bankingAccountRepository.save(bankingAccount);
        
        return ActivateAccountResult.from(bankingAccount);
    }
}