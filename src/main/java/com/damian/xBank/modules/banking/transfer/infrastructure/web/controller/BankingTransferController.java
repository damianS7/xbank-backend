package com.damian.xBank.modules.banking.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRejectRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDetailDto;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDto;
import com.damian.xBank.modules.banking.transfer.application.mapper.BankingTransferDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferConfirm;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferCreate;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferGetAll;
import com.damian.xBank.modules.banking.transfer.application.usecase.BankingTransferReject;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingTransferController {
    private final BankingTransferGetAll bankingTransferGetAll;
    private final BankingTransferCreate bankingTransferCreate;
    private final BankingTransferConfirm bankingTransferConfirm;
    private final BankingTransferReject bankingTransferReject;

    public BankingTransferController(
            BankingTransferGetAll bankingTransferGetAll,
            BankingTransferCreate bankingTransferCreate,
            BankingTransferConfirm bankingTransferConfirm,
            BankingTransferReject bankingTransferReject
    ) {
        this.bankingTransferGetAll = bankingTransferGetAll;
        this.bankingTransferCreate = bankingTransferCreate;
        this.bankingTransferConfirm = bankingTransferConfirm;
        this.bankingTransferReject = bankingTransferReject;
    }

    // endpoint to get all transfers from current user
    @GetMapping("/banking/transfers")
    public ResponseEntity<?> getTransfers() {
        Set<BankingTransfer> transfers = bankingTransferGetAll.execute();
        Set<BankingTransferDto> transferDtoSetDto = BankingTransferDtoMapper
                .toBankingTransferDtoSet(transfers);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transferDtoSetDto);
    }

    // endpoint to submit a transfer request
    @PostMapping("/banking/transfers")
    public ResponseEntity<?> transfer(
            @RequestBody @Validated
            BankingTransferRequest request
    ) {
        BankingTransfer transfer = bankingTransferCreate.createTransfer(request);
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

        BankingTransfer transfer = bankingTransferConfirm.execute(id, request);
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
            BankingTransferRejectRequest request
    ) {

        BankingTransfer transfer = bankingTransferReject.execute(id, request);
        BankingTransferDto transferDto = BankingTransferDtoMapper.toBankingTransferDto(transfer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transferDto);
    }
}