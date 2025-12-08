package com.damian.xBank.modules.banking.card.infra.controller.admin;

import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.service.admin.AdminBankingCardManagementService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PatchMapping("/admin/banking/cards/{id}/disable")
    public ResponseEntity<?> disableBankingCard(
            @PathVariable @Positive
            Long id
    ) {

        BankingCard bankingCard = adminBankingCardManagementService.disableCard(id);
        BankingCardDto bankingCardDto = BankingCardDtoMapper.toBankingCardDto(bankingCard);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardDto);
    }
}