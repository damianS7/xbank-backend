package com.damian.xBank.modules.banking.account.controller;

import com.damian.xBank.modules.banking.account.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.service.BankingAccountOperationService;
import com.damian.xBank.modules.banking.transaction.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.model.BankingTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingAccountOperationController {
    private final BankingAccountOperationService bankingAccountOperationService;

    public BankingAccountOperationController(
            BankingAccountOperationService bankingAccountOperationService
    ) {
        this.bankingAccountOperationService = bankingAccountOperationService;
    }

    // endpoint for logged customer to transfer to another account
    @PostMapping("/banking/accounts/{id}/transfer")
    public ResponseEntity<?> transfer(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountTransferRequest request
    ) {
        BankingTransaction transaction = bankingAccountOperationService.transfer(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper.toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }

    // endpoint for logged customer to deposit into given account
    @PostMapping("/banking/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountDepositRequest request
    ) {

        BankingTransaction transaction = bankingAccountOperationService.deposit(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}

