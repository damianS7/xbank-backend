package com.damian.xBank.modules.banking.account.infrastructure.controller.admin;

import com.damian.xBank.modules.banking.account.application.dto.mapper.BankingAccountDtoMapper;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.service.admin.AdminBankingAccountManagementService;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class AdminBankingAccountManagementController {
    private final AdminBankingAccountManagementService adminBankingAccountManagementService;

    public AdminBankingAccountManagementController(
            AdminBankingAccountManagementService adminBankingAccountManagementService
    ) {
        this.adminBankingAccountManagementService = adminBankingAccountManagementService;
    }

    // endpoint for admin to update status to OPEN from a BankingAccount
    @PostMapping("/admin/banking/accounts/{id}/open")
    public ResponseEntity<?> openAccount(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountOpenRequest request
    ) {
        BankingAccount bankingAccount = adminBankingAccountManagementService.openAccount(id, request);
        BankingAccountDto bankingAccountDto = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDto);
    }

    @PostMapping("/admin/banking/accounts/{id}/close")
    public ResponseEntity<?> closeAccount(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            BankingAccountOpenRequest request
    ) {
        BankingAccount bankingAccount = adminBankingAccountManagementService.openAccount(id, request);
        BankingAccountDto bankingAccountDto = BankingAccountDtoMapper.toBankingAccountDto(bankingAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingAccountDto);
    }
}

