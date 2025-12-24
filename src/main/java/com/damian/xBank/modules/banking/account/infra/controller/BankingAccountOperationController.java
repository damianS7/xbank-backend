package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountOperationService;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BankingAccountOperationController {
    private final BankingAccountOperationService bankingAccountOperationService;

    public BankingAccountOperationController(
            BankingAccountOperationService bankingAccountOperationService
    ) {
        this.bankingAccountOperationService = bankingAccountOperationService;
    }

    // endpoint for logged customer to transfer to another account
    @PostMapping("/banking/accounts/{id}/transfer") // TODO remove
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
}

