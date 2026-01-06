package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardGetAll;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingCardController {
    private final BankingCardGetAll bankingCardGetAll;

    public BankingCardController(
            BankingCardGetAll bankingCardGetAll
    ) {
        this.bankingCardGetAll = bankingCardGetAll;
    }

    // endpoint to fetch all cards of logged customer
    @GetMapping("/banking/cards")
    public ResponseEntity<?> getCustomerBankingCards() {
        Set<BankingCard> bankingCards = bankingCardGetAll.execute();
        Set<BankingCardDto> bankingCardsDto = BankingCardDtoMapper.toBankingCardSetDTO(bankingCards);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bankingCardsDto);
    }
}