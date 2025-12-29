package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountCardManagementService;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountManagementService;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountManagementController {
    private final BankingAccountManagementService bankingAccountManagementService;
    private final BankingAccountCardManagementService bankingAccountCardManagementService;

    public BankingAccountManagementController(
            BankingAccountManagementService bankingAccountManagementService,
            BankingAccountCardManagementService bankingAccountCardManagementService
    ) {
        this.bankingAccountManagementService = bankingAccountManagementService;
        this.bankingAccountCardManagementService = bankingAccountCardManagementService;
    }

    // endpoint for logged customer to close his BankingAccount
    @PatchMapping("/banking/accounts/{id}/close")
    public ResponseEntity<?> closeAccount(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountCloseRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountManagementService.closeAccount(id, request);
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
        BankingAccount bankingAccount = bankingAccountManagementService.setAccountAlias(id, request);
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
        BankingCard bankingCard = bankingAccountCardManagementService.requestCard(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingCardDTO);
    }
}

