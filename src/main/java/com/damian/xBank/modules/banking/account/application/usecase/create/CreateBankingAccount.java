package com.damian.xBank.modules.banking.account.application.usecase.create;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

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
     * Create a BankingAccount for the logged customer.
     *
     * @param command BankingAccountCreateRequest
     * @return a newly created BankingAccount
     */
    public CreateAccountResult execute(CreateBankingAccountCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the Customer entity so we can save at the end
        final User user = userRepository.findById(currentUser.getId())
            .orElseThrow(
                () -> new UserNotFoundException(currentUser.getId())
            );

        final BankingAccount bankingAccount = bankingAccountDomainService.createAccount(
            user,
            command.type(),
            command.currency()
        );

        bankingAccountRepository.save(bankingAccount);

        return CreateAccountResult.from(bankingAccount);
    }
}