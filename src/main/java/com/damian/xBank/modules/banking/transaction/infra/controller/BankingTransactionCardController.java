package com.damian.xBank.modules.banking.transaction.infra.controller;

import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionCardService;
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
public class BankingTransactionCardController {
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingTransactionCardService bankingTransactionCardService;

    @Autowired
    public BankingTransactionCardController(
            BankingTransactionCardService bankingTransactionCardService,
            BankingTransactionAccountService bankingTransactionAccountService
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingTransactionCardService = bankingTransactionCardService;
    }

    // endpoint for logged customer to get all transactions of a BankingCard
    @GetMapping("/banking/cards/{id}/transactions")
    public ResponseEntity<?> getCardTransactions(
            @PathVariable @NotNull @Positive
            Long id,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<BankingTransaction> transactions = bankingTransactionCardService
                .getTransactions(id, pageable);
        
        Page<BankingTransactionDto> pagedTransactionsDto = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(transactions);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pagedTransactionsDto);
    }
}