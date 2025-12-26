package com.damian.xBank.modules.banking.transfer.infrastructure.controller;

import com.damian.xBank.modules.banking.account.application.service.BankingAccountOperationService;
import com.damian.xBank.modules.banking.transfer.application.dto.mapper.BankingTransferDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDetailDto;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDto;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingTransferController {
    private final BankingAccountOperationService bankingAccountOperationService;

    public BankingTransferController(
            BankingAccountOperationService bankingAccountOperationService
    ) {
        this.bankingAccountOperationService = bankingAccountOperationService;
    }

    // endpoint to submit a transfer request
    @PostMapping("/banking/transfers")
    public ResponseEntity<?> transfer(
            @RequestBody @Validated
            BankingTransferRequest request
    ) {
        BankingTransfer transfer = bankingAccountOperationService.transfer(request);
        BankingTransferDetailDto transferDto = BankingTransferDtoMapper.toBankingTransferDetailDto(transfer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transferDto);
    }

    @PostMapping("/banking/transfers/{id}/confirm")
    public ResponseEntity<?> confirm(
            @Positive @PathVariable
            Long id,
            @RequestBody @Validated
            BankingTransferConfirmRequest request
    ) {

        BankingTransfer transfer = bankingAccountOperationService.confirmTransfer(id, request);
        BankingTransferDetailDto transferDto = BankingTransferDtoMapper.toBankingTransferDetailDto(transfer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transferDto);
    }

    @PostMapping("/banking/transfers/{id}/reject")
    public ResponseEntity<?> reject(
            @Positive @PathVariable
            Long id,
            @RequestBody @Validated
            BankingTransferConfirmRequest request
    ) {

        BankingTransfer transfer = bankingAccountOperationService.rejectTransfer(id, request);
        BankingTransferDto transferDto = BankingTransferDtoMapper.toBankingTransferDto(transfer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transferDto);
    }
}