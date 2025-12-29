package com.damian.xBank.modules.banking.account.infrastructure.controller.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.application.service.admin.AdminBankingAccountOperationService;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AdminBankingAccountOperationController {
    private final AdminBankingAccountOperationService adminBankingAccountOperationService;

    public AdminBankingAccountOperationController(
            AdminBankingAccountOperationService adminBankingAccountOperationService
    ) {
        this.adminBankingAccountOperationService = adminBankingAccountOperationService;
    }

    // endpoint for logged customer to deposit into given account
    @PostMapping("/admin/banking/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountDepositRequest request
    ) {

        BankingTransaction transaction = adminBankingAccountOperationService.deposit(id, request);
        BankingTransactionDto transactionDto = BankingTransactionDtoMapper
                .toBankingTransactionDto(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionDto);
    }
}

