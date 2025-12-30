package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountSummaryDto;
import com.damian.xBank.modules.banking.account.application.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.application.usecase.BankingAccountCreate;
import com.damian.xBank.modules.banking.account.application.usecase.BankingAccountGet;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountController {
    private final BankingAccountGet bankingAccountGet;
    private final BankingAccountCreate bankingAccountCreate;

    public BankingAccountController(
            BankingAccountGet bankingAccountGet,
            BankingAccountCreate bankingAccountCreate
    ) {
        this.bankingAccountGet = bankingAccountGet;
        this.bankingAccountCreate = bankingAccountCreate;
    }

    // return all the accounts from the logged customer
    @GetMapping("/banking/accounts")
    public ResponseEntity<?> getCustomerBankingAccounts() {
        Set<BankingAccount> bankingAccounts = bankingAccountGet.execute();
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

}

