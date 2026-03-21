package com.damian.xBank.modules.banking.account.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccount;
import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.create.CreateAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.create.CreateBankingAccount;
import com.damian.xBank.modules.banking.account.application.usecase.create.CreateBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccounts;
import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccountsQuery;
import com.damian.xBank.modules.banking.account.application.usecase.get.all.GetAllUserAccountsResult;
import com.damian.xBank.modules.banking.account.application.usecase.get.summary.GetDailyBalancesByCurrency;
import com.damian.xBank.modules.banking.account.application.usecase.get.summary.GetDailyBalancesByCurrencyQuery;
import com.damian.xBank.modules.banking.account.application.usecase.get.summary.GetDailyBalancesByCurrencyResult;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCard;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardCommand;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardResult;
import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAlias;
import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAliasCommand;
import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAliasResult;
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

    /**
     * Endpoint para obtener el resumen de los balances diarios de cada moneda
     *
     * @param currency
     * @return
     */
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

    /**
     * Endpoint para obtener todas las cuentas del usuario actual
     *
     * @return GetAllUserAccountsResult
     */
    @GetMapping("/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        GetAllUserAccountsQuery query = new GetAllUserAccountsQuery();
        GetAllUserAccountsResult result = getAllUserAccounts.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.accounts());
    }

    /**
     * Endpoint para obtener que un usuario logeado solicite una nueva cuenta
     *
     * @param request CreateBankingAccountRequest
     * @return CreateAccountResult
     */
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

    /**
     * Endpoint para que el usuario actual cierre una cuenta
     *
     * @param id
     * @param request
     * @return
     */
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

    /**
     * Endpoint para que el usuario cambie el alias de su cuenta
     *
     * @param id
     * @param request
     * @return
     */
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

    /**
     * Endpoint para que el usuario solicite una tarjeta
     *
     * @param id
     * @param request
     * @return RequestCardResult
     */
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

