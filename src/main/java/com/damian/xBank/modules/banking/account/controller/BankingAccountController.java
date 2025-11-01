package com.damian.xBank.modules.banking.account.controller;

import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.modules.banking.account.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.dto.response.BankingAccountSummaryDto;
import com.damian.xBank.modules.banking.account.service.BankingAccountCardManagerService;
import com.damian.xBank.modules.banking.account.service.BankingAccountService;
import com.damian.xBank.shared.domain.BankingCard;
import com.damian.xBank.modules.banking.card.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.dto.request.BankingCardRequest;
import com.damian.xBank.modules.banking.card.dto.mapper.BankingCardDtoMapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final BankingAccountService bankingAccountService;
    private final BankingAccountCardManagerService bankingAccountCardManagerService;

    @Autowired
    public BankingAccountController(
            BankingAccountService bankingAccountService,
            BankingAccountCardManagerService bankingAccountCardManagerService
    ) {
        this.bankingAccountService = bankingAccountService;
        this.bankingAccountCardManagerService = bankingAccountCardManagerService;
    }

    // return all the accounts from the logged customer
    @GetMapping("/customers/me/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        Set<BankingAccount> bankingAccounts = bankingAccountService.getLoggedCustomerBankingAccounts();
        Set<BankingAccountSummaryDto> bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountSummaryDtoSet(
                bankingAccounts
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to request for a new BankingAccount
    @PostMapping("/customers/me/banking/accounts/request")
    public ResponseEntity<?> requestBankingAccount(
            @Validated @RequestBody
            BankingAccountCreateRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountService.createBankingAccount(request);
        BankingAccountDto bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankingAccountDTO);
    }


    // endpoint to set an alias for an account
    @PatchMapping("/customers/me/banking/accounts/{id}/alias")
    public ResponseEntity<?> setBankingAccountAlias(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountAliasUpdateRequest request
    ) {
        BankingAccount bankingAccount = bankingAccountService.setBankingAccountAlias(id, request);
        BankingAccountDto bankingAccountDTO = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDTO);
    }

    // endpoint for logged customer to request for a new BankingCard
    @PostMapping("/customers/me/banking/accounts/{id}/cards/request")
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

