package com.damian.xBank.modules.payment.checkout.infrastructure;

import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutGet;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutSubmit;
import com.damian.xBank.modules.payment.checkout.domain.PaymentCheckoutForm;
import com.damian.xBank.modules.payment.checkout.domain.excepcion.PaymentCheckoutException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Validated
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
    public String paymentCheckout(
            @PathVariable @Positive Long id,
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
    public String paymentStatus(
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

    @PostMapping("/payments/checkout")
    public String paymentCheckoutSubmit(
            PaymentCheckoutForm form, // TODO Validate?
            Model model
    ) {
        log.debug("Processing payment with id: {}", form.getPaymentId());
        if (form.getPaymentId() == null) {
            model.addAttribute("error", "Invalid payment id");
            return "layout/error";
        }

        try {
            paymentCheckoutSubmit.execute(
                    new PaymentCheckoutSubmitRequest(
                            form.getPaymentId(),
                            form.getCardHolder(),
                            form.getCardNumber(),
                            form.getCvv(),
                            form.getCardPin(),
                            form.getExpiryMonth(),
                            form.getExpiryYear(),
                            form.getMerchantCallbackUrl()
                    )
            );
        } catch (Exception exception) {
            throw new PaymentCheckoutException(form.getPaymentId(), exception.getMessage());
        }

        return "redirect:/payments/" + form.getPaymentId() + "/status";
    }

    @ExceptionHandler(PaymentCheckoutException.class)
    public String handleException(PaymentCheckoutException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("paymentId", ex.getResourceId());
        return "layout/error";
    }

    @ExceptionHandler(WebClientRequestException.class)
    public String handleException(WebClientRequestException ex, Model model) {
        model.addAttribute("error", "Internal server error");
        return "layout/error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleException(HttpRequestMethodNotSupportedException ex, Model model) {
        model.addAttribute("error", "Internal server error");
        return "layout/error";
    }
}