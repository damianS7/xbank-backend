package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.card.application.cqrs.command.ActivateBankingCardCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.command.LockBankingCardCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.command.SetBankingCardDailyLimitCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.command.SetBankingCardPinCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.command.UnlockBankingCardCommand;
import com.damian.xBank.modules.banking.card.application.cqrs.result.BankingCardResult;
import com.damian.xBank.modules.banking.card.application.usecase.ActivateBankingCard;
import com.damian.xBank.modules.banking.card.application.usecase.LockBankingCard;
import com.damian.xBank.modules.banking.card.application.usecase.SetBankingCardDailyLimit;
import com.damian.xBank.modules.banking.card.application.usecase.SetBankingCardPin;
import com.damian.xBank.modules.banking.card.application.usecase.UnlockBankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.ActivateBankingCardRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.LockBankingCardRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.SetBankingCardDailyLimitRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.SetBankingCardPinRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request.UnlockBankingCardRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingCardManagementController {
    private final SetBankingCardPin setBankingCardPin;
    private final SetBankingCardDailyLimit setBankingCardDailyLimit;
    private final LockBankingCard lockBankingCard;
    private final UnlockBankingCard unlockBankingCard;
    private final ActivateBankingCard activateBankingCard;

    @Autowired
    public BankingCardManagementController(
        SetBankingCardPin setBankingCardPin,
        SetBankingCardDailyLimit setBankingCardDailyLimit,
        LockBankingCard lockBankingCard,
        UnlockBankingCard unlockBankingCard,
        ActivateBankingCard activateBankingCard
    ) {
        this.setBankingCardPin = setBankingCardPin;
        this.setBankingCardDailyLimit = setBankingCardDailyLimit;
        this.lockBankingCard = lockBankingCard;
        this.unlockBankingCard = unlockBankingCard;
        this.activateBankingCard = activateBankingCard;
    }

    // endpoint for logged customer to set PIN on a BankingCard
    @PatchMapping("/banking/cards/{id}/pin")
    public ResponseEntity<?> updatePin(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        SetBankingCardPinRequest request
    ) {
        SetBankingCardPinCommand command = new SetBankingCardPinCommand(
            id,
            request.pin(),
            request.password()
        );

        BankingCardResult result = setBankingCardPin.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to set a daily limit
    @PatchMapping("/banking/cards/{id}/daily-limit")
    public ResponseEntity<?> updateDailyLimit(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        SetBankingCardDailyLimitRequest request
    ) {
        SetBankingCardDailyLimitCommand command = new SetBankingCardDailyLimitCommand(
            id,
            request.dailyLimit(),
            request.password()
        );
        BankingCardResult result = setBankingCardDailyLimit.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/lock")
    public ResponseEntity<?> lock(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        LockBankingCardRequest request
    ) {
        LockBankingCardCommand command = new LockBankingCardCommand(id, request.password());
        BankingCardResult result = lockBankingCard.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/unlock")
    public ResponseEntity<?> unlock(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        UnlockBankingCardRequest request
    ) {
        UnlockBankingCardCommand command = new UnlockBankingCardCommand(id, request.password());
        BankingCardResult result = unlockBankingCard.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for BankingCard activation
    @PatchMapping("/banking/cards/{id}/activate")
    public ResponseEntity<?> activate(
        @PathVariable @Positive
        Long id,
        @Valid @RequestBody
        ActivateBankingCardRequest request
    ) {
        ActivateBankingCardCommand command = new ActivateBankingCardCommand(id, request.cvv());
        BankingCardResult result = activateBankingCard.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}