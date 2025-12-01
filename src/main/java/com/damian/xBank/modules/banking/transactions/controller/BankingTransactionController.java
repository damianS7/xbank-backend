package com.damian.xBank.modules.banking.transactions.controller;

import com.damian.xBank.modules.banking.transactions.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transactions.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transactions.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transactions.service.BankingTransactionCardService;
import com.damian.xBank.shared.domain.BankingTransaction;
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
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingTransactionCardService bankingTransactionCardService;

    @Autowired
    public BankingTransactionController(
            BankingTransactionCardService bankingTransactionCardService,
            BankingTransactionAccountService bankingTransactionAccountService
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingTransactionCardService = bankingTransactionCardService;
    }

    // endpoint for logged customer to get a single BankingTransaction by id
    @GetMapping("/banking/transactions/{id}")
    public ResponseEntity<?> getBankingTransaction(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        BankingTransaction transaction = bankingTransactionAccountService.getBankingTransaction(id);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDto);
    }

    // endpoint for logged customer to get all transactions of a BankingCard
    @GetMapping("/banking/cards/{id}/transactions")
    public ResponseEntity<?> getBankingCardTransactions(
            @PathVariable @NotNull @Positive
            Long id,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionCardService
                .getTransactions(id, pageable);
        Page<BankingTransactionDto> transactionDTOS = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDTOS);
    }

    // endpoint for logged customer to get all transactions of a BankingAccount
    @GetMapping("/banking/accounts/{id}/transactions")
    public ResponseEntity<?> getBankingAccountTransactions(
            @PathVariable @NotNull @Positive
            Long id,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionAccountService.getTransactions(id, pageable);
        Page<BankingTransactionDto> transactionDTOS = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionDTOS);
    }
}

