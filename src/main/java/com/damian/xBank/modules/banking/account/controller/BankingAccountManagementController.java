package com.damian.xBank.modules.banking.account.controller;

import com.damian.xBank.modules.banking.account.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.service.BankingAccountCardManagerService;
import com.damian.xBank.modules.banking.account.service.BankingAccountManagementService;
import com.damian.xBank.modules.banking.card.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.dto.request.BankingCardRequest;
import com.damian.xBank.modules.banking.card.dto.response.BankingCardDto;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.BankingCard;
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

    // endpoint to set an alias for an account
    @PatchMapping("/banking/accounts/{id}/alias")
    public ResponseEntity<?> setBankingAccountAlias(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountAliasUpdateRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountManagementService.setBankingAccountAlias(id, request);
        BankingAccountDto bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/banking/accounts/{id}/cards/request")
    public ResponseEntity<?> customerRequestBankingCard(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardRequest request
    ) {
        BankingCard bankingCard = bankingAccountCardManagerService.requestBankingCard(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingCardDTO);
    }
}

