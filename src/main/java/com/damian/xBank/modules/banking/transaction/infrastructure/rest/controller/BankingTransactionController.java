package com.damian.xBank.modules.banking.transaction.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionDetailResult;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.account.GetAccountTransactions;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.account.GetAccountTransactionsQuery;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.byid.GetTransaction;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.byid.GetTransactionQuery;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.card.GetCardTransactions;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.card.GetCardTransactionsQuery;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.pending.GetPendingTransactions;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.pending.GetPendingTransactionsQuery;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingTransactionController {
    private final GetTransaction getTransaction;
    private final GetCardTransactions getCardTransactions;
    private final GetAccountTransactions getAccountTransactions;
    private final GetPendingTransactions getPendingTransactions;

    @Autowired
    public BankingTransactionController(
        GetTransaction getTransaction,
        GetCardTransactions getCardTransactions,
        GetAccountTransactions getAccountTransactions,
        GetPendingTransactions getPendingTransactions
    ) {
        this.getTransaction = getTransaction;
        this.getCardTransactions = getCardTransactions;
        this.getAccountTransactions = getAccountTransactions;
        this.getPendingTransactions = getPendingTransactions;
    }

    // endpoint for logged customer to get a single BankingTransaction by id
    @GetMapping("/banking/transactions/{id}")
    public ResponseEntity<?> getTransaction(
        @PathVariable @NotNull @Positive
        Long id
    ) {
        GetTransactionQuery query = new GetTransactionQuery(id);
        BankingTransactionDetailResult result = getTransaction.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to get all pending transactions of a BankingAccount
    @GetMapping("/banking/transactions/pending")
    public ResponseEntity<?> getPendingTransactions(
        @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        GetPendingTransactionsQuery query = new GetPendingTransactionsQuery(pageable);
        PageResult<BankingTransactionResult> result = getPendingTransactions.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to get all transactions of a BankingCard
    @GetMapping("/banking/cards/{id}/transactions")
    public ResponseEntity<?> getCardTransactions(
        @PathVariable @Positive
        Long id,
        @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        GetCardTransactionsQuery query = new GetCardTransactionsQuery(id, pageable);

        PageResult<BankingTransactionResult> result = getCardTransactions.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to get all transactions of a BankingAccount
    @GetMapping("/banking/accounts/{id}/transactions")
    public ResponseEntity<?> getAccountTransactions(
        @PathVariable @Positive
        Long id,
        @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        GetAccountTransactionsQuery query = new GetAccountTransactionsQuery(id, pageable);
        PageResult<BankingTransactionResult> result = getAccountTransactions.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}

