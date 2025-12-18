package com.damian.xBank.modules.banking.transaction.application.guard;

import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionOwnershipException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;

public class BankingTransactionGuard {
    private final BankingTransaction transaction;

    public BankingTransactionGuard(BankingTransaction transaction) {
        this.transaction = transaction;
    }

    public static BankingTransactionGuard forTransaction(BankingTransaction transaction) {
        return new BankingTransactionGuard(transaction);
    }

    /**
     * Validate the ownership of the {@link #transaction}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransactionOwnershipException if the account does not belong to the customer
     */
    public BankingTransactionGuard assertOwnership(Customer customer) {

        // compare account owner id with given customer id
        if (!transaction.belongsTo(customer)) {
            throw new BankingTransactionOwnershipException(
                    transaction.getId(), customer.getId()
            );
        }

        return this;
    }
}