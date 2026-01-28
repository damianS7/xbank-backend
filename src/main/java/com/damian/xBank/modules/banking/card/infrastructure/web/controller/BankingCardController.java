package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.application.dto.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.usecase.AuthorizeCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.BankingCardGetAll;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class BankingCardController {
    private final BankingCardGetAll bankingCardGetAll;
    private final AuthorizeCardPayment authorizeCardPayment;

    public BankingCardController(
            BankingCardGetAll bankingCardGetAll,
            AuthorizeCardPayment authorizeCardPayment
    ) {
        this.bankingCardGetAll = bankingCardGetAll;
        this.authorizeCardPayment = authorizeCardPayment;
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

    // endpoint for card authorization
    @PostMapping("/banking/cards/authorize")
    public ResponseEntity<?> authorizeCard(
            @Validated @RequestBody
            AuthorizeCardPaymentRequest request
    ) {
        PaymentAuthorizationResponse response = authorizeCardPayment.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // endpoint for card authorization
    @PostMapping("/banking/cards/capture")
    public ResponseEntity<?> capturePayment(
            @Validated @RequestBody
            AuthorizeCardPaymentRequest request
    ) {
        PaymentAuthorizationResponse response = authorizeCardPayment.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}