package com.damian.xBank.modules.banking.account.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.service.BankingAccountNumberGenerator;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import org.springframework.stereotype.Service;

@Service // TODO check if this annotation make sense to be here since its a spring annotation
public class BankingAccountDomainService {
    private final BankingAccountNumberGenerator bankingAccountNumberGenerator;

    public BankingAccountDomainService(
            BankingAccountNumberGenerator bankingAccountNumberGenerator
    ) {
        this.bankingAccountNumberGenerator = bankingAccountNumberGenerator;
    }

    /**
     * Create a BankingAccount for a specific customer.
     *
     * @param customer        Customer owner of the BankingAccount
     * @param accountType     the type of BankingAccount
     * @param accountCurrency the currency of the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount createAccount(
            Customer customer,
            BankingAccountType accountType,
            BankingAccountCurrency accountCurrency
    ) {
        return BankingAccount
                .create(customer)
                .setStatus(BankingAccountStatus.PENDING_ACTIVATION)
                .setAccountType(accountType)
                .setCurrency(accountCurrency)
                .setAccountNumber(bankingAccountNumberGenerator.generate());
    }
}