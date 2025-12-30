package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.service.BankingAccountDomainService;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountGetCards {
    private final BankingAccountDomainService bankingAccountDomainService;
    private final CustomerRepository customerRepository;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;

    public BankingAccountGetCards(
            BankingAccountDomainService bankingAccountDomainService,
            BankingAccountRepository bankingAccountRepository,
            CustomerRepository customerRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingAccountDomainService = bankingAccountDomainService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.customerRepository = customerRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Create a BankingAccount for the logged customer.
     *
     * @param request BankingAccountCreateRequest the request containing the data needed
     *                to create the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount execute(BankingAccountCreateRequest request) {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // we get the Customer entity so we can save at the end
        final Customer customer = customerRepository.findById(currentCustomer.getId()).orElseThrow(
                () -> new CustomerNotFoundException(currentCustomer.getId())
        );

        BankingAccount bankingAccount = bankingAccountDomainService.createAccount(
                customer,
                request.type(),
                request.currency()
        );

        return bankingAccountRepository.save(bankingAccount);
    }
}