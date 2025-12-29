package com.damian.xBank.modules.banking.transaction.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class BankingTransactionController {
    private final BankingTransactionService bankingTransactionService;

    @Autowired
    public BankingTransactionController(
            BankingTransactionService bankingTransactionService
    ) {
        this.bankingTransactionService = bankingTransactionService;
    }

    // endpoint for logged customer to get a single BankingTransaction by id
    @GetMapping("/banking/transactions/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        BankingTransaction transaction = bankingTransactionService.getTransaction(id);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDto);
    }

    // endpoint for logged customer to get all pending transactions of a BankingAccount
    @GetMapping("/banking/transactions/pending")
    public ResponseEntity<?> getPendingTransactions(
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionService.getAccountPendingTransactions(pageable);
        Page<BankingTransactionDto> transactionDtoList = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDtoList);
    }

    // endpoint for logged customer to get all transactions of a BankingCard
    @GetMapping("/banking/cards/{id}/transactions")
    public ResponseEntity<?> getCardTransactions(
            @PathVariable @NotNull @Positive
            Long id,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionService
                .getCardTransactions(id, pageable);

        Page<BankingTransactionDto> pagedTransactionsDto = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pagedTransactionsDto);
    }

    // endpoint for logged customer to get all transactions of a BankingAccount
    @GetMapping("/banking/accounts/{id}/transactions")
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable @NotNull @Positive
            Long id,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionService.getAccountTransactions(id, pageable);
        Page<BankingTransactionDto> transactionDTOS = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDTOS);
    }
}

