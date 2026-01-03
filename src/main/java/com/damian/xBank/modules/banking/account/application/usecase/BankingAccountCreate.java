package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.UserNotFoundException;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountCreate {
    private final BankingAccountDomainService bankingAccountDomainService;
    private final UserAccountRepository userRepository;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public BankingAccountCreate(
            BankingAccountDomainService bankingAccountDomainService,
            BankingAccountRepository bankingAccountRepository,
            UserAccountRepository userRepository,
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
     * @param request BankingAccountCreateRequest
     * @return a newly created BankingAccount
     */
    public BankingAccount execute(BankingAccountCreateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the Customer entity so we can save at the end
        final User user = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new UserNotFoundException(currentUser.getId())
        );

        BankingAccount bankingAccount = bankingAccountDomainService.createAccount(
                user,
                request.type(),
                request.currency()
        );

        return bankingAccountRepository.save(bankingAccount);
    }
}