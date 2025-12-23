package com.damian.xBank.modules.banking.card.infrastructure.controller;

import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.service.BankingCardManagementService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingCardManagementController {
    private final BankingCardManagementService bankingCardManagementService;

    @Autowired
    public BankingCardManagementController(
            BankingCardManagementService bankingCardManagementService
    ) {
        this.bankingCardManagementService = bankingCardManagementService;
    }

    // endpoint for logged customer to set PIN on a BankingCard
    @PatchMapping("/banking/cards/{id}/pin")
    public ResponseEntity<?> updatePin(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUpdatePinRequest request
    ) {
        BankingCard bankingCard = bankingCardManagementService.updatePin(id, request);
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
        BankingCard bankingCard = bankingCardManagementService.updateDailyLimit(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/lock-status")
    public ResponseEntity<?> updateLock(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUpdateLockRequest request
    ) {
        BankingCard bankingCard = bankingCardManagementService.updateLockStatus(
                id,
                request
        );
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }
}