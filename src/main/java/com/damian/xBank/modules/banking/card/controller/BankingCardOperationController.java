package com.damian.xBank.modules.banking.card.controller;

import com.damian.xBank.modules.banking.card.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.service.BankingCardOperationService;
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
public class BankingCardOperationController {
    private final BankingCardOperationService bankingCardOperationService;

    public BankingCardOperationController(
            BankingCardOperationService bankingCardOperationService
    ) {
        this.bankingCardOperationService = bankingCardOperationService;
    }

    // endpoint for logged customer to withdraw from card
    @PostMapping("/banking/cards/{id}/withdraw")
    public ResponseEntity<?> deposit(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardWithdrawRequest request
    ) {

        BankingTransaction transaction = bankingCardOperationService.withdraw(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }

    // endpoint for logged customer to withdraw from card
    @PostMapping("/banking/cards/{id}/spend")
    public ResponseEntity<?> spend(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardSpendRequest request
    ) {

        BankingTransaction transaction = bankingCardOperationService.spend(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}