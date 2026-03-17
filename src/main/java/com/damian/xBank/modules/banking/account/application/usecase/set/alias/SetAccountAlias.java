package com.damian.xBank.modules.banking.account.application.usecase.set.alias;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para cambiar el alias de una cuenta.
 */
@Service
public class SetAccountAlias {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public SetAccountAlias(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * @param command Comando con los datos requeridos
     *                                                                                           TODO cambiar a void?
     */
    public SetAccountAliasResult execute(SetAccountAliasCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Buscar la cuenta a la que se cambiará el alias
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.accountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(command.accountId())
            );

        // Si no es admin comprueba que sea el owner de la cuenta.
        if (!currentUser.isAdmin()) {
            bankingAccount.assertOwnedBy(currentUser.getId());
        }

        // Cambiar el alias
        bankingAccount.changeAlias(command.alias());
        bankingAccountRepository.save(bankingAccount);

        return SetAccountAliasResult.from(bankingAccount);
    }
}