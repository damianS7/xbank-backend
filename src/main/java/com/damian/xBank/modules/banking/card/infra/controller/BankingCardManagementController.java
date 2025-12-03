package com.damian.xBank.modules.banking.card.infra.controller;

import com.damian.xBank.modules.auth.application.dto.PasswordConfirmationRequest;
import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSetDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSetLockStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSetPinRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingCardManagementController {
    private final BankingCardService bankingCardService;

    @Autowired
    public BankingCardManagementController(
            BankingCardService bankingCardService
    ) {
        this.bankingCardService = bankingCardService;
    }

    // endpoint for logged customer to cancel a BankingCard
    @PatchMapping("/banking/cards/{id}/cancel")
    public ResponseEntity<?> cancelBankingCard(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            PasswordConfirmationRequest request
    ) {
        BankingCard bankingCard = bankingCardService.cancelCard(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to set PIN on a BankingCard
    @PatchMapping("/banking/cards/{id}/pin")
    public ResponseEntity<?> setPinBankingCard(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardSetPinRequest request
    ) {
        BankingCard bankingCard = bankingCardService.setBankingCardPin(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to set a daily limit
    @PatchMapping("/banking/cards/{id}/daily-limit")
    public ResponseEntity<?> customerSetDailyLimitBankingCard(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardSetDailyLimitRequest request
    ) {
        BankingCard bankingCard = bankingCardService.setDailyLimit(id, request);
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }

    // endpoint for logged customer to lock or unlock a BankingCard
    @PatchMapping("/banking/cards/{id}/lock-status")
    public ResponseEntity<?> customerLockBankingCard(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardSetLockStatusRequest request
    ) {
        BankingCard bankingCard = bankingCardService.setCardLockStatus(
                id,
                request
        );
        BankingCardDto bankingCardDTO = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDTO);
    }
}