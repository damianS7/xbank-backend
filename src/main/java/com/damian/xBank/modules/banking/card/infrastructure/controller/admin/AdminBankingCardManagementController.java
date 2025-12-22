package com.damian.xBank.modules.banking.card.infrastructure.controller.admin;

import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.service.admin.AdminBankingCardManagementService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class AdminBankingCardManagementController {
    private static final Logger log = LoggerFactory.getLogger(AdminBankingCardManagementController.class);
    private final AdminBankingCardManagementService adminBankingCardManagementService;

    @Autowired
    public AdminBankingCardManagementController(
            AdminBankingCardManagementService adminBankingCardManagementService
    ) {
        this.adminBankingCardManagementService = adminBankingCardManagementService;
    }

    // endpoint for logged customer to disable a BankingCard
    @PatchMapping("/admin/banking/cards/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            BankingCardUpdateStatusRequest request
    ) {

        BankingCard bankingCard = adminBankingCardManagementService.updateStatus(id, request);
        BankingCardDto bankingCardDto = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDto);
    }
}