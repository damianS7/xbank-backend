package com.damian.xBank.modules.banking.account.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.application.cqrs.command.CloseBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.cqrs.command.CreateBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.cqrs.command.RequestBankingAccountCardCommand;
import com.damian.xBank.modules.banking.account.application.cqrs.command.SetBankingAccountAliasCommand;
import com.damian.xBank.modules.banking.account.application.cqrs.query.GetAllBankingAccountsQuery;
import com.damian.xBank.modules.banking.account.application.cqrs.query.GetDailyBalancesByCurrencyQuery;
import com.damian.xBank.modules.banking.account.application.cqrs.result.BankingAccountResult;
import com.damian.xBank.modules.banking.account.application.cqrs.result.DailyBalancesByCurrencyResult;
import com.damian.xBank.modules.banking.account.application.usecase.CloseBankingAccount;
import com.damian.xBank.modules.banking.account.application.usecase.CreateBankingAccount;
import com.damian.xBank.modules.banking.account.application.usecase.GetAllBankingAccounts;
import com.damian.xBank.modules.banking.account.application.usecase.GetDailyBalancesByCurrency;
import com.damian.xBank.modules.banking.account.application.usecase.RequestBankingAccountCard;
import com.damian.xBank.modules.banking.account.application.usecase.SetBankingAccountAlias;
import com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request.CloseBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request.CreateBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request.RequestBankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request.SetBankingAccountAliasRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
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

import java.util.Set;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final GetAllBankingAccounts getAllBankingAccounts;
    private final CreateBankingAccount createBankingAccount;
    private final RequestBankingAccountCard bankingAccountCardRequest;
    private final CloseBankingAccount closeBankingAccount;
    private final SetBankingAccountAlias setBankingAccountAlias;
    private final GetDailyBalancesByCurrency getDailyBalancesByCurrency;

    public BankingAccountController(
        GetAllBankingAccounts getAllBankingAccounts,
        CreateBankingAccount createBankingAccount,
        RequestBankingAccountCard bankingAccountCardRequest,
        CloseBankingAccount closeBankingAccount,
        SetBankingAccountAlias setBankingAccountAlias,
        GetDailyBalancesByCurrency getDailyBalancesByCurrency
    ) {
        this.getAllBankingAccounts = getAllBankingAccounts;
        this.createBankingAccount = createBankingAccount;
        this.bankingAccountCardRequest = bankingAccountCardRequest;
        this.closeBankingAccount = closeBankingAccount;
        this.setBankingAccountAlias = setBankingAccountAlias;
        this.getDailyBalancesByCurrency = getDailyBalancesByCurrency;
    }

    // endpoint to get summary for account currency
    @GetMapping("/banking/accounts/summary/{currency}")
    public ResponseEntity<?> accountSummaryByCurrency(
        @PathVariable @NotNull
        String currency
    ) {
        GetDailyBalancesByCurrencyQuery query = new GetDailyBalancesByCurrencyQuery(currency);
        DailyBalancesByCurrencyResult result = getDailyBalancesByCurrency.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.dailyBalances());
    }

    // return all the accounts from the logged customer
    @GetMapping("/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        GetAllBankingAccountsQuery query = new GetAllBankingAccountsQuery();
        Set<BankingAccountResult> bankingAccountsResult = getAllBankingAccounts.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(bankingAccountsResult);
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

        BankingAccountResult bankingAccountResult = createBankingAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(bankingAccountResult);
    }

    // endpoint for logged customer to close his BankingAccount
    @PatchMapping("/banking/accounts/{id}/close")
    public ResponseEntity<?> closeAccount(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        CloseBankingAccountRequest request
    ) {
        CloseBankingAccountCommand command = new CloseBankingAccountCommand(
            id,
            request.password()
        );
        BankingAccountResult bankingAccountResult = closeBankingAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(bankingAccountResult);
    }

    // endpoint to set an alias for an account
    @PatchMapping("/banking/accounts/{id}/alias")
    public ResponseEntity<?> setAccountAlias(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        SetBankingAccountAliasRequest request
    ) {
        SetBankingAccountAliasCommand command = new SetBankingAccountAliasCommand(id, request.alias());
        BankingAccountResult bankingAccountResult = setBankingAccountAlias.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(bankingAccountResult);
    }

    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/banking/accounts/{id}/cards")
    public ResponseEntity<?> requestCard(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        RequestBankingAccountCardRequest request
    ) {
        RequestBankingAccountCardCommand command = new RequestBankingAccountCardCommand(id, request.type());
        BankingCard bankingCard = bankingAccountCardRequest.execute(command);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(bankingCardDTO);
    }

}

