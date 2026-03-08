package com.damian.xBank.modules.banking.account.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.application.usecase.account.card.RequestCard;
import com.damian.xBank.modules.banking.account.application.usecase.account.card.RequestCardCommand;
import com.damian.xBank.modules.banking.account.application.usecase.account.card.RequestCardResult;
import com.damian.xBank.modules.banking.account.application.usecase.account.close.CloseAccount;
import com.damian.xBank.modules.banking.account.application.usecase.account.close.CloseAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.account.close.CloseAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.account.create.CreateAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.account.create.CreateBankingAccount;
import com.damian.xBank.modules.banking.account.application.usecase.account.create.CreateBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.all.GetAllUserAccounts;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.all.GetAllUserAccountsQuery;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.all.GetAllUserAccountsResult;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.summary.GetDailyBalancesByCurrency;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.summary.GetDailyBalancesByCurrencyQuery;
import com.damian.xBank.modules.banking.account.application.usecase.account.get.summary.GetDailyBalancesByCurrencyResult;
import com.damian.xBank.modules.banking.account.application.usecase.account.set.alias.SetAccountAlias;
import com.damian.xBank.modules.banking.account.application.usecase.account.set.alias.SetAccountAliasCommand;
import com.damian.xBank.modules.banking.account.application.usecase.account.set.alias.SetAccountAliasResult;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.CloseBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.CreateBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.RequestBankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.SetBankingAccountAliasRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final GetAllUserAccounts getAllUserAccounts;
    private final CreateBankingAccount createBankingAccount;
    private final RequestCard bankingAccountCardRequest;
    private final CloseAccount closeAccount;
    private final SetAccountAlias setAccountAlias;
    private final GetDailyBalancesByCurrency getDailyBalancesByCurrency;

    public BankingAccountController(
        GetAllUserAccounts getAllUserAccounts,
        CreateBankingAccount createBankingAccount,
        RequestCard bankingAccountCardRequest,
        CloseAccount closeAccount,
        SetAccountAlias setAccountAlias,
        GetDailyBalancesByCurrency getDailyBalancesByCurrency
    ) {
        this.getAllUserAccounts = getAllUserAccounts;
        this.createBankingAccount = createBankingAccount;
        this.bankingAccountCardRequest = bankingAccountCardRequest;
        this.closeAccount = closeAccount;
        this.setAccountAlias = setAccountAlias;
        this.getDailyBalancesByCurrency = getDailyBalancesByCurrency;
    }

    // endpoint to get summary for account currency
    @GetMapping("/banking/accounts/summary/{currency}")
    public ResponseEntity<?> accountSummaryByCurrency(
        @PathVariable @NotNull
        String currency
    ) {
        GetDailyBalancesByCurrencyQuery query = new GetDailyBalancesByCurrencyQuery(currency);
        GetDailyBalancesByCurrencyResult result = getDailyBalancesByCurrency.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.dailyBalances());
    }

    // return all the accounts from the logged customer
    @GetMapping("/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        GetAllUserAccountsQuery query = new GetAllUserAccountsQuery();
        GetAllUserAccountsResult result = getAllUserAccounts.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.accounts());
    }

    // endpoint for logged customer to request for a new BankingAccount
    @PostMapping("/banking/accounts")
    public ResponseEntity<?> requestBankingAccount(
        @Valid @RequestBody
        CreateBankingAccountRequest request
    ) {
        CreateBankingAccountCommand command = new CreateBankingAccountCommand(
            request.type(),
            request.currency()
        );

        CreateAccountResult result = createBankingAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

    // endpoint for logged customer to close his BankingAccount
    @PatchMapping("/banking/accounts/{id}/close")
    public ResponseEntity<?> closeAccount(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        CloseBankingAccountRequest request
    ) {
        CloseAccountCommand command = new CloseAccountCommand(
            id,
            request.password()
        );
        CloseAccountResult result = closeAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint to set an alias for an account
    @PatchMapping("/banking/accounts/{id}/alias")
    public ResponseEntity<?> setAccountAlias(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        SetBankingAccountAliasRequest request
    ) {
        SetAccountAliasCommand command = new SetAccountAliasCommand(id, request.alias());
        SetAccountAliasResult result = setAccountAlias.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/banking/accounts/{id}/cards")
    public ResponseEntity<?> requestCard(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        RequestBankingAccountCardRequest request
    ) {
        RequestCardCommand command = new RequestCardCommand(id, request.type());
        RequestCardResult result = bankingAccountCardRequest.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

}

