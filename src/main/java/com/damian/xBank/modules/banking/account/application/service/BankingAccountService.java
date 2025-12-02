package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.model.Customer;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BankingAccountService {
    private final CustomerRepository customerRepository;
    private final BankingAccountRepository bankingAccountRepository;
    private final Faker faker;

    public BankingAccountService(
            BankingAccountRepository bankingAccountRepository,
            CustomerRepository customerRepository,
            Faker faker
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.customerRepository = customerRepository;
        this.faker = faker;
    }

    /**
     * Return all the BankingAccounts that belongs to the logged customer.
     *
     * @return Set of BankingAccounts
     */
    public Set<BankingAccount> getLoggedCustomerBankingAccounts() {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        return this.getCustomerBankingAccounts(currentCustomer.getId());
    }

    /**
     * Return all the BankingAccounts that belongs to a specific customer.
     *
     * @param customerId ID of the customer
     * @return Set of BankingAccounts
     */
    public Set<BankingAccount> getCustomerBankingAccounts(Long customerId) {
        return bankingAccountRepository.findByCustomer_Id(customerId);
    }

    /**
     * Create a BankingAccount for the logged customer.
     *
     * @param request BankingAccountCreateRequest the request containing the data needed
     *                to create the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount createBankingAccount(BankingAccountCreateRequest request) {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        return this.createBankingAccount(currentCustomer.getId(), request);
    }

    /**
     * Create a BankingAccount for a specific customer.
     *
     * @param customerId ID of the customer
     * @param request    BankingAccountCreateRequest the request containing the data needed
     *                   to create the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount createBankingAccount(Long customerId, BankingAccountCreateRequest request) {
        // we get the Customer entity so we can save at the end
        final Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(
                        Exceptions.CUSTOMER.NOT_FOUND, customerId
                )
        );

        return this.createBankingAccount(customer, request.type(), request.currency());
    }

    /**
     * Create a BankingAccount for a specific customer.
     *
     * @param customer        Customer owner of the BankingAccount
     * @param accountType     the type of BankingAccount
     * @param accountCurrency the currency of the BankingAccount
     * @return a newly created BankingAccount
     */
    private BankingAccount createBankingAccount(
            Customer customer,
            BankingAccountType accountType,
            BankingAccountCurrency accountCurrency
    ) {
        BankingAccount bankingAccount = BankingAccount.create()
                                                      .setAccountStatus(BankingAccountStatus.CLOSED)
                                                      .setOwner(customer)
                                                      .setAccountType(accountType)
                                                      .setAccountCurrency(accountCurrency)
                                                      .setAccountNumber(this.generateAccountNumber());

        return bankingAccountRepository.save(bankingAccount);
    }


    /**
     * Generate a random account number.
     *
     * @return String account number
     */
    public String generateAccountNumber() {
        //ES00 0000 0000 0000 0000 0000
        String country = faker.country().countryCode2().toUpperCase();
        return country + faker.number().digits(22);
    }
}