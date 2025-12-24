package com.damian.xBank.modules.banking.transfer.infrastructure.controller;

import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.application.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingTransferController {
    private final BankingTransferService bankingTransferService;

    public BankingTransferController(
            BankingTransferService bankingTransferService
    ) {
        this.bankingTransferService = bankingTransferService;
    }

    // endpoint to submit a transfer request
    @PostMapping("/banking/transfers")
    public ResponseEntity<?> transfer(
            @RequestBody @Validated
            BankingTransferRequest request
    ) {
        BankingTransfer transfer = bankingTransferService.initiate(request);
        //        BankingTransferDto transferDto = BankingCardDtoMapper.toBankingCardSetDTO(bankingCards);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("transferDto");
    }

    @GetMapping("/banking/transfers/{id}")
    public ResponseEntity<?> confirm() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("");
    }

    @GetMapping("/banking/transfers/{id}")
    public ResponseEntity<?> reject() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("");
    }
}