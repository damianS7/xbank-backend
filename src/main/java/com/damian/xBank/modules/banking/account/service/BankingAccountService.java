package com.damian.xBank.modules.banking.account.service;

import com.damian.xBank.modules.banking.account.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.customer.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.Customer;
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

    // return all the BankingAccounts that belongs to the logged customer.
    public Set<BankingAccount> getLoggedCustomerBankingAccounts() {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        return this.getCustomerBankingAccounts(currentCustomer.getId());
    }

    // return all the BankingAccounts that belongs to customerId.
    public Set<BankingAccount> getCustomerBankingAccounts(Long customerId) {
        return bankingAccountRepository.findByCustomer_Id(customerId);
    }


    private BankingAccount createBankingAccount(
            Customer customerOwner,
            BankingAccountType accountType,
            BankingAccountCurrency accountCurrency
    ) {
        BankingAccount bankingAccount = new BankingAccount();
        bankingAccount.setAccountStatus(BankingAccountStatus.CLOSED);
        bankingAccount.setOwner(customerOwner);
        bankingAccount.setAccountType(accountType);
        bankingAccount.setAccountCurrency(accountCurrency);
        bankingAccount.setAccountNumber(this.generateAccountNumber());
        return bankingAccountRepository.save(bankingAccount);
    }

    // (admin) create a BankingAccount for a specific customer
    public BankingAccount createBankingAccount(Long customerId, BankingAccountCreateRequest request) {
        // we get the Customer entity so we can save at the end
        final Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(
                        Exceptions.CUSTOMER.NOT_FOUND, customerId
                )
        );

        return this.createBankingAccount(customer, request.type(), request.currency());
    }

    // create a BankingAccount for the logged customer
    public BankingAccount createBankingAccount(BankingAccountCreateRequest request) {
        // we extract the customer logged from the SecurityContext
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        return this.createBankingAccount(currentCustomer.getId(), request);
    }

    public String generateAccountNumber() {
        //ES00 0000 0000 0000 0000 0000
        String country = faker.country().countryCode2().toUpperCase();
        return country + faker.number().digits(22);
    }
}