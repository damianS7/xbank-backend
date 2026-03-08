package com.damian.xBank.modules.banking.account.infrastructure.rest.controller.admin;

import com.damian.xBank.modules.banking.account.application.usecase.account.deposit.DepositAccount;
import com.damian.xBank.modules.banking.account.application.usecase.account.deposit.DepositAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.account.deposit.DepositAccountResult;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.DepositBankingAccountRequest;
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
    private final DepositAccount depositAccount;

    public AdminBankingAccountController(
        DepositAccount depositAccount
    ) {
        this.depositAccount = depositAccount;
    }

    // endpoint for logged customer to deposit into given account
    @PostMapping("/admin/banking/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(
        @PathVariable @NotNull @Positive
        Long id,
        @Valid @RequestBody
        DepositBankingAccountRequest request
    ) {
        DepositAccountCommand command = new DepositAccountCommand(
            id,
            request.depositorName(),
            request.amount()
        );

        DepositAccountResult result = depositAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
}

