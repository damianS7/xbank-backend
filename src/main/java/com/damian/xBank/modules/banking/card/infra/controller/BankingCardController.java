package com.damian.xBank.modules.banking.card.infra.controller;

import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingCardController {
    private final BankingCardService bankingCardService;

    public BankingCardController(
            BankingCardService bankingCardService
    ) {
        this.bankingCardService = bankingCardService;
    }

    // endpoint to fetch all cards of logged customer
    @GetMapping("/banking/cards")
    public ResponseEntity<?> getCustomerBankingCards() {
        Set<BankingCard> bankingCards = bankingCardService.getCustomerBankingCards();
        Set<BankingCardDto> bankingCardsDto = BankingCardDtoMapper.toBankingCardSetDTO(bankingCards);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardsDto);
    }
}