package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardLockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUnlockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardLock;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardSetDailyLimit;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardSetPin;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardUnlock;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingCardManagementController {
    private final BankingCardSetPin bankingCardSetPin;
    private final BankingCardSetDailyLimit bankingCardSetDailyLimit;
    private final BankingCardLock bankingCardLock;
    private final BankingCardUnlock bankingCardUnlock;

    @Autowired
    public BankingCardManagementController(
            BankingCardSetPin bankingCardSetPin,
            BankingCardSetDailyLimit bankingCardSetDailyLimit,
            BankingCardLock bankingCardLock,
            BankingCardUnlock bankingCardUnlock
    ) {
        this.bankingCardSetPin = bankingCardSetPin;
        this.bankingCardSetDailyLimit = bankingCardSetDailyLimit;
        this.bankingCardLock = bankingCardLock;
        this.bankingCardUnlock = bankingCardUnlock;
    }

    // endpoint for logged customer to set PIN on a BankingCard
    @PatchMapping("/banking/cards/{id}/pin")
    public ResponseEntity<?> updatePin(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUpdatePinRequest request
    ) {
        BankingCard bankingCard = bankingCardSetPin.execute(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to set a daily limit
    @PatchMapping("/banking/cards/{id}/daily-limit")
    public ResponseEntity<?> updateDailyLimit(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUpdateDailyLimitRequest request
    ) {
        BankingCard bankingCard = bankingCardSetDailyLimit.execute(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/lock")
    public ResponseEntity<?> lock(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardLockRequest request
    ) {
        BankingCard bankingCard = bankingCardLock.execute(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/unlock")
    public ResponseEntity<?> unlock(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUnlockRequest request
    ) {
        BankingCard bankingCard = bankingCardUnlock.execute(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }
}