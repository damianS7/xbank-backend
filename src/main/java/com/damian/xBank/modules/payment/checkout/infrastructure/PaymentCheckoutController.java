package com.damian.xBank.modules.payment.checkout.infrastructure;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidCvvException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidPinException;
import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutGet;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutSubmit;
import com.damian.xBank.modules.payment.checkout.domain.PaymentCheckoutForm;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentCheckoutController {
    private static final Logger log = LoggerFactory.getLogger(PaymentCheckoutController.class);
    private final PaymentCheckoutGet paymentCheckoutGet;
    private final PaymentCheckoutSubmit paymentCheckoutSubmit;

    public PaymentCheckoutController(
            PaymentCheckoutGet paymentCheckoutGet,
            PaymentCheckoutSubmit paymentCheckoutSubmit
    ) {
        this.paymentCheckoutGet = paymentCheckoutGet;
        this.paymentCheckoutSubmit = paymentCheckoutSubmit;
    }

    @GetMapping("/payments/{id}/checkout")
    public String showPaymentForm(
            @PathVariable Long id,
            Model model
    ) {
        // Get payment
        PaymentIntent paymentIntent = paymentCheckoutGet.execute(id);
        if (paymentIntent.getStatus() != PaymentIntentStatus.PENDING) {
            return "redirect:/payments/" + id + "/status";
        }

        // form
        PaymentCheckoutForm form = new PaymentCheckoutForm();
        form.setPaymentId(paymentIntent.getId());

        // read only fields
        model.addAttribute("paymentId", paymentIntent.getId());
        model.addAttribute("status", paymentIntent.getStatus());
        model.addAttribute("PaymentIntentStatus", PaymentIntentStatus.class);
        model.addAttribute("merchant", paymentIntent.getMerchantName());
        model.addAttribute("amount", paymentIntent.getAmount());
        model.addAttribute("currency", paymentIntent.getCurrency());
        model.addAttribute("merchantCallbackUrl", paymentIntent.getMerchantCallbackUrl());
        model.addAttribute("form", form);
        return "layout/main";
    }

    @GetMapping("/payments/{id}/status")
    public String showPaymentStatus(
            @PathVariable Long id,
            Model model
    ) {
        // Get payment
        PaymentIntent paymentIntent = paymentCheckoutGet.execute(id);

        // read only fields
        model.addAttribute("paymentId", paymentIntent.getId());
        model.addAttribute("status", paymentIntent.getStatus());
        model.addAttribute("PaymentIntentStatus", PaymentIntentStatus.class);
        model.addAttribute("merchant", paymentIntent.getMerchantName());
        model.addAttribute("amount", paymentIntent.getAmount());
        model.addAttribute("currency", paymentIntent.getCurrency());
        model.addAttribute("merchantCallbackUrl", paymentIntent.getMerchantCallbackUrl());
        return "layout/status";
    }

    @PostMapping("/payments/{id}/checkout")
    public String processPayment(
            @PathVariable @Positive Long id,
            PaymentCheckoutForm form
    ) {
        log.debug("Processing payment with id: {}", form.getPaymentId());

        // Process payment
        paymentCheckoutSubmit.execute(
                new PaymentCheckoutSubmitRequest(
                        id,
                        form.getCardNumber(),
                        form.getCvv(),
                        form.getCardPin(),
                        form.getExpiryMonth(),
                        form.getExpiryYear(),
                        form.getMerchantCallbackUrl()
                )
        );

        return "redirect:/payments/" + id + "/status";
    }

    @ExceptionHandler(BankingCardInvalidCvvException.class)
    public String handleInvalidCvv(BankingCardInvalidCvvException ex, Model model) {
        model.addAttribute("error", "Invalid CVV");
        return "layout/main";
    }

    @ExceptionHandler(BankingCardInvalidPinException.class)
    public String handleInvalidPin(BankingCardInvalidPinException ex, Model model) {
        model.addAttribute("error", "Invalid PIN");
        return "layout/main";
    }
}