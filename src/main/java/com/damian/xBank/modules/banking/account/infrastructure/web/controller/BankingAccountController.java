package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountSummaryDto;
import com.damian.xBank.modules.banking.account.application.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.application.usecase.*;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final BankingAccountGetAll bankingAccountGetAll;
    private final BankingAccountCreate bankingAccountCreate;
    private final BankingAccountCardCreate bankingAccountCardCreate;
    private final BankingAccountClose bankingAccountClose;
    private final BankingAccountSetAlias bankingAccountSetAlias;

    public BankingAccountController(
            BankingAccountGetAll bankingAccountGetAll,
            BankingAccountCreate bankingAccountCreate,
            BankingAccountCardCreate bankingAccountCardCreate,
            BankingAccountClose bankingAccountClose,
            BankingAccountSetAlias bankingAccountSetAlias
    ) {
        this.bankingAccountGetAll = bankingAccountGetAll;
        this.bankingAccountCreate = bankingAccountCreate;
        this.bankingAccountCardCreate = bankingAccountCardCreate;
        this.bankingAccountClose = bankingAccountClose;
        this.bankingAccountSetAlias = bankingAccountSetAlias;
    }

    // return all the accounts from the logged customer
    @GetMapping("/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        Set<BankingAccount> bankingAccounts = bankingAccountGetAll.execute();
        Set<BankingAccountSummaryDto> bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountSummaryDtoSet(
                bankingAccounts
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to request for a new BankingAccount
    @PostMapping("/banking/accounts")
    public ResponseEntity<?> requestBankingAccount(
            @Validated @RequestBody
            BankingAccountCreateRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountCreate.execute(request);
        BankingAccountDto bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to close his BankingAccount
    @PatchMapping("/banking/accounts/{id}/close")
    public ResponseEntity<?> closeAccount(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountCloseRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountClose.execute(id, request);
        BankingAccountDto bankingAccountDto = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDto);
    }

    // endpoint to set an alias for an account
    @PatchMapping("/banking/accounts/{id}/alias")
    public ResponseEntity<?> setAccountAlias(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountAliasUpdateRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountSetAlias.execute(id, request);
        BankingAccountDto bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/banking/accounts/{id}/cards")
    public ResponseEntity<?> requestCard(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountCardRequest request
    ) {
        BankingCard bankingCard = bankingAccountCardCreate.execute(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingCardDTO);
    }

}

