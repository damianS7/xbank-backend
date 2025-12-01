package com.damian.xBank.modules.banking.account.controller;

import com.damian.xBank.modules.banking.account.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.dto.response.BankingAccountSummaryDto;
import com.damian.xBank.modules.banking.account.service.BankingAccountCardManagerService;
import com.damian.xBank.modules.banking.account.service.BankingAccountManagementService;
import com.damian.xBank.modules.banking.account.service.BankingAccountService;
import com.damian.xBank.shared.domain.BankingAccount;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final BankingAccountService bankingAccountService;
    private final BankingAccountManagementService bankingAccountManagementService;
    private final BankingAccountCardManagerService bankingAccountCardManagerService;

    public BankingAccountController(
            BankingAccountService bankingAccountService,
            BankingAccountManagementService bankingAccountManagementService,
            BankingAccountCardManagerService bankingAccountCardManagerService
    ) {
        this.bankingAccountService = bankingAccountService;
        this.bankingAccountManagementService = bankingAccountManagementService;
        this.bankingAccountCardManagerService = bankingAccountCardManagerService;
    }

    // return all the accounts from the logged customer
    @GetMapping("/banking/accounts")
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
    @PostMapping("/banking/accounts/request")
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

}

