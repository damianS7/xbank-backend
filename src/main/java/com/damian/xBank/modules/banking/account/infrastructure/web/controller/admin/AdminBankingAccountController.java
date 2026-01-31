package com.damian.xBank.modules.banking.account.infrastructure.web.controller.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.application.usecase.BankingAccountDeposit;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1")
public class AdminBankingAccountController {
    private final BankingAccountDeposit bankingAccountDeposit;

    public AdminBankingAccountController(
            BankingAccountDeposit bankingAccountDeposit
    ) {
        this.bankingAccountDeposit = bankingAccountDeposit;
    }

    // endpoint for logged customer to deposit into given account
    @PostMapping("/admin/banking/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable @NotNull @Positive
            Long id,
            @Valid @RequestBody
            BankingAccountDepositRequest request
    ) {

        BankingTransaction transaction = bankingAccountDeposit.execute(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}

