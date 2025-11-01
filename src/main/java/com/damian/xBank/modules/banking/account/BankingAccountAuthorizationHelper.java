package com.damian.xBank.modules.banking.account;

import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.exception.BankingAccountAuthorizationException;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountAuthorizationHelper {
    private Customer customer;
    private BankingAccount account;

    public static BankingAccountAuthorizationHelper authorize(Customer customer, BankingAccount account) {
        BankingAccountAuthorizationHelper helper = new BankingAccountAuthorizationHelper();
        helper.account = account;
        helper.customer = customer;
        return helper;
    }

    /**
     * Check if the BankingCard belongs to this customer
     *
     * @return BankingAccountAuthorizationHelper
     */
    public BankingAccountAuthorizationHelper checkOwner() {
        //         check if the account to be closed belongs to this customer.
        if (!account.getOwner().getId().equals(customer.getId())) {
            // banking account does not belong to this customer
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.ACCOUNT.ACCESS_FORBIDDEN
            );
        }
        return this;
    }


    /**
     * Check if account is not disabled or locked
     *
     * @return BankingAccountAuthorizationHelper
     */
    public BankingAccountAuthorizationHelper checkAccountStatus() {
        //         check account status
        final boolean isAccountClosed = account.getAccountStatus().equals(BankingAccountStatus.CLOSED);
        if (isAccountClosed) {
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.ACCOUNT.CLOSED
            );
        }

        final boolean isAccountSuspended = account.getAccountStatus().equals(BankingAccountStatus.SUSPENDED);
        if (isAccountSuspended) {
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.ACCOUNT.SUSPENDED
            );
        }
        return this;
    }
}
