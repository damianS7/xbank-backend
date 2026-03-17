package com.damian.xBank.modules.banking.account.application.usecase.create;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para abrir/crear nuevas cuentas bancarias por parte del usuario actual.
 */
@Service
public class CreateBankingAccount {
    private final BankingAccountDomainService bankingAccountDomainService;
    private final UserRepository userRepository;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public CreateBankingAccount(
        BankingAccountDomainService bankingAccountDomainService,
        BankingAccountRepository bankingAccountRepository,
        UserRepository userRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingAccountDomainService = bankingAccountDomainService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * @param command Comando con los datos requeridos para abrir la cuenta.
     * @return Los datos de la cuenta que se ha creado.
     */
    @Transactional
    public CreateAccountResult execute(CreateBankingAccountCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Usuario actual
        final User user = userRepository.findById(currentUser.getId())
            .orElseThrow(
                () -> new UserNotFoundException(currentUser.getId())
            );

        // Creación de la nueva cuenta
        BankingAccount bankingAccount = bankingAccountDomainService.createAccount(
            user,
            command.type(),
            command.currency()
        );

        bankingAccountRepository.save(bankingAccount);

        return CreateAccountResult.from(bankingAccount);
    }
}