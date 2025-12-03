package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.modules.banking.account.application.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountCardManagerService;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountManagementService;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardRequest;
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
    private final BankingAccountCardManagerService bankingAccountCardManagerService;

    public BankingAccountManagementController(
            BankingAccountManagementService bankingAccountManagementService,
            BankingAccountCardManagerService bankingAccountCardManagerService
    ) {
        this.bankingAccountManagementService = bankingAccountManagementService;
        this.bankingAccountCardManagerService = bankingAccountCardManagerService;
    }

    // endpoint for logged customer to close his BankingAccount
    @PostMapping("/banking/accounts/{id}/close")
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

    // TODO create BankingAccountCardManagerController??
    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/banking/accounts/{id}/cards/request")
    public ResponseEntity<?> requestCard(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardRequest request
    ) {
        BankingCard bankingCard = bankingAccountCardManagerService.requestCard(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingCardDTO);
    }
}

