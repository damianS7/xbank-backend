package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPaymentCommand;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPaymentCommand;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserCardsQuery;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPaymentResult;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCardsResult;
import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPayment;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCards;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.CaptureCardPaymentRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class BankingCardController {
    private final GetAllCurrentUserBankingCards getAllCurrentUserBankingCards;
    private final AuthorizeCardPayment authorizeCardPayment;
    private final CaptureCardPayment captureCardPayment;

    public BankingCardController(
        GetAllCurrentUserBankingCards getAllCurrentUserBankingCards,
        AuthorizeCardPayment authorizeCardPayment,
        CaptureCardPayment captureCardPayment
    ) {
        this.getAllCurrentUserBankingCards = getAllCurrentUserBankingCards;
        this.authorizeCardPayment = authorizeCardPayment;
        this.captureCardPayment = captureCardPayment;
    }

    // endpoint to fetch all cards of logged customer
    @GetMapping("/banking/cards")
    public ResponseEntity<?> getCustomerBankingCards() {
        GetAllCurrentUserCardsQuery query = new GetAllCurrentUserCardsQuery();
        GetAllCurrentUserBankingCardsResult result = getAllCurrentUserBankingCards.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.cards());
    }

    // endpoint for card authorization
    @PostMapping("/banking/cards/authorize")
    public ResponseEntity<?> authorizeCard(
        @Valid @RequestBody
        AuthorizeCardPaymentRequest request
    ) {
        AuthorizeCardPaymentCommand command = new AuthorizeCardPaymentCommand(
            request.merchant(),
            request.cardHolder(),
            request.cardNumber(),
            request.expiryMonth(),
            request.expiryYear(),
            request.cvv(),
            request.amount()
        );

        PaymentAuthorizationResponse result = authorizeCardPayment.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    // endpoint for card authorization
    @PostMapping("/banking/cards/capture")
    public ResponseEntity<?> capturePayment(
        @Valid @RequestBody
        CaptureCardPaymentRequest request
    ) {
        CaptureCardPaymentCommand command = new CaptureCardPaymentCommand(
            request.authorizationId()
        );
        CaptureCardPaymentResult result = captureCardPayment.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}