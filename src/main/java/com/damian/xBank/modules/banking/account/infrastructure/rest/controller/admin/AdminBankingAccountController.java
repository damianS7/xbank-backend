package com.damian.xBank.modules.banking.account.infrastructure.rest.controller.admin;

import com.damian.xBank.modules.banking.account.application.cqrs.command.DepositBankingAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.DepositBankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request.DepositBankingAccountRequest;
import com.damian.xBank.modules.banking.transaction.application.cqrs.result.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class AdminBankingAccountController {
    private final DepositBankingAccount depositBankingAccount;

    public AdminBankingAccountController(
        DepositBankingAccount depositBankingAccount
    ) {
        this.depositBankingAccount = depositBankingAccount;
    }

    // endpoint for logged customer to deposit into given account
    @PostMapping("/admin/banking/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        DepositBankingAccountRequest request
    ) {
        DepositBankingAccountCommand command = new DepositBankingAccountCommand(
            id,
            request.depositorName(),
            request.amount()
        );

        BankingTransaction transaction = depositBankingAccount.execute(command);
        BankingTransactionResult transactionDto = BankingTransactionDtoMapper
            .toBankingTransactionResult(transaction);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(transactionDto);
    }
}

