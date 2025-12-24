package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AdminBankingAccountManagementService {
    private final AuthenticationContext authenticationContext;
    private final BankingAccountRepository bankingAccountRepository;
    private final PasswordValidator passwordValidator;

    public AdminBankingAccountManagementService(
            AuthenticationContext authenticationContext,
            BankingAccountRepository bankingAccountRepository,
            PasswordValidator passwordValidator
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingAccountRepository = bankingAccountRepository;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Update the status of a banking account
     *
     * @param customer       the customer who changes the status
     * @param bankingAccount the banking account to update
     * @param accountStatus  the new status
     * @return the updated banking account
     */
    private BankingAccount updateStatus(
            Customer customer,
            BankingAccount bankingAccount,
            BankingAccountStatus accountStatus
    ) {
        // business rules only for customers
        if (customer.getAccount().getRole() == UserAccountRole.CUSTOMER) {

            bankingAccount.assertOwnedBy(customer.getId())
                          .assertNotSuspended()
                          .assertNotClosed();
        }

        // we mark the account as closed
        bankingAccount.setAccountStatus(accountStatus);

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the status and return BankingAccount
        return bankingAccountRepository.save(bankingAccount);
    }

    /**
     * Change the status of a banking account to ACTIVE
     *
     * @param bankingAccountId the id of the banking account to be opened
     * @return the updated banking account
     */
    public BankingAccount openAccount(
            Long bankingAccountId,
            BankingAccountOpenRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to be open
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId).orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                bankingAccountId
                        ) // Banking account not found
                );

        return this.updateStatus(currentCustomer, bankingAccount, BankingAccountStatus.ACTIVE);
    }

    /**
     * Change the status of a banking account to CLOSED
     *
     * @param bankingAccountId the id of the banking account to be closed
     * @return the updated banking account
     */
    public BankingAccount closeAccount(
            Long bankingAccountId,
            BankingAccountCloseRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to be closed
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());

        return this.updateStatus(currentCustomer, bankingAccount, BankingAccountStatus.CLOSED);
    }

    /**
     * Set alias for a banking account
     *
     * @param customer       the customer who changes the alias
     * @param bankingAccount the banking account to update
     * @param alias          the new alias
     * @return the updated banking account
     */
    private BankingAccount setAccountAlias(
            Customer customer,
            BankingAccount bankingAccount,
            String alias
    ) {
        // business rules only for customers
        if (!customer.isAdmin()) {

            bankingAccount.assertOwnedBy(customer.getId())
                          .assertActive();

        }

        // we mark the account as closed
        bankingAccount.setAlias(alias);

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingAccountRepository.save(bankingAccount);
    }

    /**
     * Set alias for a banking account
     *
     * @param bankingAccountId the id of the banking account to set alias
     * @param request          the request with the new alias
     * @return the updated banking account
     */
    public BankingAccount setAccountAlias(
            Long bankingAccountId,
            BankingAccountAliasUpdateRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking account to set alias
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        //        authenticationContext.validatePassword(currentCustomer.getAccount(), request.password());
        return this.setAccountAlias(currentCustomer, bankingAccount, request.alias());
    }
}