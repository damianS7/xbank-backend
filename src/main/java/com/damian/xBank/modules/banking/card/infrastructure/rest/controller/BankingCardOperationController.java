package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.card.application.cqrs.command.WithdrawFromATMCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.result.WithdrawFromATMResult;
import com.damian.xBank.modules.banking.card.application.usecase.WithdrawFromATM;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.WithdrawFromATMRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1")
@RestController
public class BankingCardOperationController {
    private final WithdrawFromATM withdrawFromATM;

    public BankingCardOperationController(
        WithdrawFromATM withdrawFromATM
    ) {
        this.withdrawFromATM = withdrawFromATM;
    }

    // endpoint for logged customer to withdraw from card
    @PostMapping("/banking/cards/{id}/withdraw")
    public ResponseEntity<?> withdraw(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        WithdrawFromATMRequest request
    ) {
        WithdrawFromATMCommand command = new WithdrawFromATMCommand(
            id,
            request.amount(),
            request.cardPIN()
        );

        WithdrawFromATMResult result = withdrawFromATM.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
}