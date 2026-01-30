package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardWithdraw;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingCardOperationController {
    private final BankingCardWithdraw bankingCardWithdraw;

    public BankingCardOperationController(
            BankingCardWithdraw bankingCardWithdraw
    ) {
        this.bankingCardWithdraw = bankingCardWithdraw;
    }

    // endpoint for logged customer to withdraw from card
    @PostMapping("/banking/cards/{id}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable @Positive
            Long id,
            @Valid @RequestBody
            BankingCardWithdrawRequest request
    ) {
        BankingTransaction transaction = bankingCardWithdraw.execute(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}