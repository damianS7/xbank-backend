package com.damian.xBank.modules.banking.account.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountDomainService {
    private final BankingAccountNumberGenerator bankingAccountNumberGenerator;

    public BankingAccountDomainService(
        BankingAccountNumberGenerator bankingAccountNumberGenerator
    ) {
        this.bankingAccountNumberGenerator = bankingAccountNumberGenerator;
    }

    /**
     * Create a BankingAccount for a specific user.
     *
     * @param user            Customer owner of the BankingAccount
     * @param accountType     the type of BankingAccount
     * @param accountCurrency the currency of the BankingAccount
     * @return a newly created BankingAccount
     */
    public BankingAccount createAccount(
        User user,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency
    ) {
        String accountNumber = bankingAccountNumberGenerator.generate();
        return BankingAccount.create(user, accountNumber, accountType, accountCurrency);
    }
}