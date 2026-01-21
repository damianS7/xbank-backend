package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardSpend;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardWithdraw;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingCardOperationController {
    private final BankingCardSpend bankingCardSpend;
    private final BankingCardWithdraw bankingCardWithdraw;

    public BankingCardOperationController(
            BankingCardSpend bankingCardSpend,
            BankingCardWithdraw bankingCardWithdraw
    ) {
        this.bankingCardSpend = bankingCardSpend;
        this.bankingCardWithdraw = bankingCardWithdraw;
    }

    // endpoint for logged customer to withdraw from card
    @PostMapping("/banking/cards/{id}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardWithdrawRequest request
    ) {

        BankingTransaction transaction = bankingCardWithdraw.execute(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }

    // endpoint for logged customer to spend from card
    @PostMapping("/banking/cards/{id}/spend")
    public ResponseEntity<?> spend(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingCardSpendRequest request
    ) {

        BankingTransaction transaction = bankingCardSpend.execute(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}