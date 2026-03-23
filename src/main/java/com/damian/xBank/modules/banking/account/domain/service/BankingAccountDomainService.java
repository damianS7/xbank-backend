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
     * Crea una cuenta bancaria
     *
     * @param user            El owner de la cuenta
     * @param accountType     Tipo de cuenta a crear
     * @param accountCurrency Moneda de la cuenta
     * @return La cuenta creada
     */
    public BankingAccount createAccount(
        User user,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency
    ) {
        String accountNumber = bankingAccountNumberGenerator.generate();
        return BankingAccount.create(user, accountNumber, accountType, accountCurrency);
    }

    public boolean isExternalBIN(String BIN) {
        return BIN.startsWith(bankingAccountNumberGenerator.getBIN());
    }
}