package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATM;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATMCommand;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATMResult;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.WithdrawFromATMRequest;
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

    /**
     * Endpoint para hacer retiros
     *
     * @param id      de la tarjeta sobre la que se va a hacer el retiro
     * @param request Petición con los datos requeridos
     * @return La transacción generada para la operación.
     */
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