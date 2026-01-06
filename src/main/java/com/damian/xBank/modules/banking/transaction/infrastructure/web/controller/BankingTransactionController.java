package com.damian.xBank.modules.banking.transaction.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.usecase.BankingTransactionAccountGet;
import com.damian.xBank.modules.banking.transaction.application.usecase.BankingTransactionCardGet;
import com.damian.xBank.modules.banking.transaction.application.usecase.BankingTransactionGet;
import com.damian.xBank.modules.banking.transaction.application.usecase.BankingTransactionGetPending;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
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
    private final BankingTransactionGet bankingTransactionGet;
    private final BankingTransactionCardGet bankingTransactionCardGet;
    private final BankingTransactionAccountGet bankingTransactionAccountGet;
    private final BankingTransactionGetPending bankingTransactionGetPending;

    @Autowired
    public BankingTransactionController(
            BankingTransactionGet bankingTransactionGet,
            BankingTransactionCardGet bankingTransactionCardGet,
            BankingTransactionAccountGet bankingTransactionAccountGet,
            BankingTransactionGetPending bankingTransactionGetPending
    ) {
        this.bankingTransactionGet = bankingTransactionGet;
        this.bankingTransactionCardGet = bankingTransactionCardGet;
        this.bankingTransactionAccountGet = bankingTransactionAccountGet;
        this.bankingTransactionGetPending = bankingTransactionGetPending;
    }

    // endpoint for logged customer to get a single BankingTransaction by id
    @GetMapping("/banking/transactions/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        BankingTransaction transaction = bankingTransactionGet.execute(id);
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
        Page<BankingTransaction> transactions = bankingTransactionGetPending.execute(pageable);
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
        Page<BankingTransaction> transactions = bankingTransactionCardGet
                .execute(id, pageable);

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
        Page<BankingTransaction> transactions = bankingTransactionAccountGet.execute(id, pageable);
        Page<BankingTransactionDto> transactionDTOS = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDTOS);
    }
}

