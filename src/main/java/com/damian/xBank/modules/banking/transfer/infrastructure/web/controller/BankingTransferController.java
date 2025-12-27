package com.damian.xBank.modules.banking.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDetailDto;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDto;
import com.damian.xBank.modules.banking.transfer.application.mapper.BankingTransferDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferConfirmUseCase;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferCreateUseCase;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferRejectUseCase;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class BankingTransferController {
    private final BankingTransferCreateUseCase bankingTransferCreateUseCase;
    private final BankingTransferConfirmUseCase bankingTransferConfirmUseCase;
    private final BankingTransferRejectUseCase bankingTransferRejectUseCase;

    public BankingTransferController(
            BankingTransferCreateUseCase bankingTransferCreateUseCase,
            BankingTransferConfirmUseCase bankingTransferConfirmUseCase,
            BankingTransferRejectUseCase bankingTransferRejectUseCase
    ) {
        this.bankingTransferCreateUseCase = bankingTransferCreateUseCase;
        this.bankingTransferConfirmUseCase = bankingTransferConfirmUseCase;
        this.bankingTransferRejectUseCase = bankingTransferRejectUseCase;
    }

    // endpoint to submit a transfer request
    @PostMapping("/banking/transfers")
    public ResponseEntity<?> transfer(
            @RequestBody @Validated
            BankingTransferRequest request
    ) {
        BankingTransfer transfer = bankingTransferCreateUseCase.transfer(request);
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

        BankingTransfer transfer = bankingTransferConfirmUseCase.confirmTransfer(id, request);
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

        BankingTransfer transfer = bankingTransferRejectUseCase.rejectTransfer(id, request);
        BankingTransferDto transferDto = BankingTransferDtoMapper.toBankingTransferDto(transfer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transferDto);
    }
}