package com.damian.xBank.modules.payment.checkout.infrastructure.web.controller;

import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutSubmit;
import com.damian.xBank.modules.payment.checkout.domain.PaymentCheckoutForm;
import com.damian.xBank.modules.payment.checkout.domain.excepcion.PaymentCheckoutException;
import com.damian.xBank.modules.payment.intent.application.usecase.GetPaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import com.damian.xBank.shared.exception.ErrorCodes;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.HashMap;
import java.util.Map;

@Validated
@Controller
public class PaymentCheckoutController {
    private static final Logger log = LoggerFactory.getLogger(PaymentCheckoutController.class);
    private final GetPaymentIntent getPaymentIntent;
    private final PaymentCheckoutSubmit paymentCheckoutSubmit;
    private final MessageSource messageSource;

    public PaymentCheckoutController(
            GetPaymentIntent getPaymentIntent,
            PaymentCheckoutSubmit paymentCheckoutSubmit,
            MessageSource messageSource
    ) {
        this.getPaymentIntent = getPaymentIntent;
        this.paymentCheckoutSubmit = paymentCheckoutSubmit;
        this.messageSource = messageSource;
    }

    @GetMapping("/payments/{id}/checkout")
    public String paymentCheckout(
            @PathVariable @Positive Long id,
            Model model
    ) {
        // Get payment
        PaymentIntent paymentIntent = getPaymentIntent.execute(id);
        if (paymentIntent.getStatus() != PaymentIntentStatus.PENDING) {
            return "redirect:/payments/" + id + "/status";
        }

        // form
        PaymentCheckoutForm form = new PaymentCheckoutForm();

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
        PaymentIntent paymentIntent = getPaymentIntent.execute(id);

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
            @Valid PaymentCheckoutForm form,
            Model model
    ) {
        log.debug("Processing payment: {}", form.paymentId());
        if (form.paymentId() == null) {
            model.addAttribute("error", "Invalid payment id");
            return "layout/error";
        }

        try {
            paymentCheckoutSubmit.execute(
                    new PaymentCheckoutSubmitRequest(
                            form.paymentId(),
                            form.cardHolder(),
                            form.cardNumber(),
                            form.cvv(),
                            form.cardPin(),
                            form.expiryMonth(),
                            form.expiryYear()
                    )
            );
        } catch (Exception exception) {
            throw new PaymentCheckoutException(form.paymentId(), exception.getMessage());
        }

        return "redirect:/payments/" + form.paymentId() + "/status";
    }

    @ExceptionHandler(PaymentCheckoutException.class)
    public String handleException(PaymentCheckoutException ex, Model model) {
        log.warn("Payment checkout failed: {}", ex.getMessage());
        model.addAttribute("paymentId", ex.getResourceId());
        model.addAttribute("error", ex.getMessage());
        return "layout/error";
    }

    @ExceptionHandler(WebClientRequestException.class)
    public String handleException(WebClientRequestException ex, Model model) {
        log.warn("Payment checkout connection failed: {}", ex.getMessage());
        model.addAttribute("error", "Internal server error");
        return "layout/error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleException(HttpRequestMethodNotSupportedException ex, Model model) {
        model.addAttribute("error", "Internal server error");
        return "layout/error";
    }

    @ExceptionHandler(ConstraintViolationException.class) // 404
    public String handleException(
            ConstraintViolationException ex,
            Model model
    ) {
        String message = messageSource.getMessage(
                ErrorCodes.VALIDATION_FAILED,
                null,
                LocaleContextHolder.getLocale()
        );
        log.warn("Validation error: {}", ex.getMessage(), ex);
        model.addAttribute("error", message);
        return "layout/error";
    }

    @ExceptionHandler(HandlerMethodValidationException.class) // 404
    public String handleException(
            HandlerMethodValidationException ex,
            Model model
    ) {
        String message = messageSource.getMessage(
                ErrorCodes.VALIDATION_FAILED,
                null,
                LocaleContextHolder.getLocale()
        );
        log.warn("Validation error: {}", ex.getMessage(), ex);
        model.addAttribute("error", message);
        return "layout/error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 400
    public String handleException(
            MethodArgumentNotValidException ex,
            Model model
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed: {} errors -> {}", errors.size(), errors);
        model.addAttribute(
                "error", messageSource.getMessage(
                        ErrorCodes.VALIDATION_FAILED,
                        null,
                        LocaleContextHolder.getLocale()
                )
        );
        model.addAttribute("errors", errors);
        return "layout/error";
    }
}