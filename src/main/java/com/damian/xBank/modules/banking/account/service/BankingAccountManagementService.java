package com.damian.xBank.modules.banking.account.service;

import com.damian.xBank.modules.banking.account.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.account.validator.BankingAccountValidator;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BankingAccountManagementService {
    private final BankingAccountRepository bankingAccountRepository;

    public BankingAccountManagementService(
            BankingAccountRepository bankingAccountRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
    }


    private BankingAccount updateBankingAccountStatus(
            BankingAccount bankingAccount,
            BankingAccountStatus accountStatus
    ) {
        // we mark the account as closed
        bankingAccount.setAccountStatus(accountStatus);

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingAccountRepository.save(bankingAccount);
    }

    // (admin) open a BankingAccount
    public BankingAccount openBankingAccount(Long bankingAccountId) {
        // Banking account to to open
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        return this.updateBankingAccountStatus(bankingAccount, BankingAccountStatus.ACTIVE);
    }

    // Logged customer open a BankingAccount
    // TODO rename request to BankingAccountStatusRequest???
    public BankingAccount openBankingAccount(
            Long bankingAccountId,
            BankingAccountOpenRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking account to be open
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        // TODO maybe this should be removed?
        // check if the account belongs to this customer.
        BankingAccountValidator
                .validate(bankingAccount)
                .ownership(currentCustomer)
                .active();

        // validate password
        AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());

        return this.updateBankingAccountStatus(bankingAccount, BankingAccountStatus.ACTIVE);
    }

    // (admin) close a BankingAccount
    public BankingAccount closeBankingAccount(Long bankingAccountId) {
        // Banking account to to close
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        return this.updateBankingAccountStatus(bankingAccount, BankingAccountStatus.CLOSED);
    }

    // Logged customer close a BankingAccount
    public BankingAccount closeBankingAccount(
            Long bankingAccountId,
            BankingAccountCloseRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking account to be closed
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        // TODO maybe this should be removed?
        // check if the account belongs to this customer.
        BankingAccountValidator
                .validate(bankingAccount)
                .ownership(currentCustomer)
                .active();

        AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());

        return this.updateBankingAccountStatus(bankingAccount, BankingAccountStatus.CLOSED);
    }

    // set an alias for an account
    private BankingAccount setBankingAccountAlias(BankingAccount bankingAccount, String alias) {
        // we mark the account as closed
        bankingAccount.setAlias(alias);

        // we change the updateAt timestamp field
        bankingAccount.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingAccountRepository.save(bankingAccount);
    }

    // (admin) set an alias for an account
    public BankingAccount setBankingAccountAlias(
            Long bankingAccountId,
            String alias
    ) {

        // Banking account to set an alias
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        return this.setBankingAccountAlias(bankingAccount, alias);
    }

    // Logged customer set an alias for an account
    public BankingAccount setBankingAccountAlias(
            Long bankingAccountId,
            BankingAccountAliasUpdateRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking account to set alias
        final BankingAccount bankingAccount = bankingAccountRepository.findById(bankingAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        bankingAccountId
                ) // Banking account not found
        );

        // check if the account belongs to this customer.
        BankingAccountValidator
                .validate(bankingAccount)
                .ownership(currentCustomer);

        //        AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());
        return this.setBankingAccountAlias(bankingAccount, request.alias());
    }
}