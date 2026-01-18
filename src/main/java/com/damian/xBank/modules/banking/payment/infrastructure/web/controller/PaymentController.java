package com.damian.xBank.modules.banking.payment.infrastructure.web.controller;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidCvvException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidPinException;
import com.damian.xBank.modules.banking.payment.application.usecase.PaymentGet;
import com.damian.xBank.modules.banking.payment.application.usecase.PaymentSubmit;
import com.damian.xBank.modules.banking.payment.domain.model.Payment;
import com.damian.xBank.modules.banking.payment.domain.model.PaymentForm;
import com.damian.xBank.modules.banking.payment.domain.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentGet paymentGet;
    private final PaymentSubmit paymentSubmit;

    public PaymentController(
            PaymentGet paymentGet,
            PaymentSubmit paymentSubmit
    ) {
        this.paymentGet = paymentGet;
        this.paymentSubmit = paymentSubmit;
    }

    @GetMapping("/payments/{id}")
    public String showPaymentForm(
            @PathVariable Long id,
            Model model
    ) {
        PaymentForm form = new PaymentForm();

        // Get payment
        Payment payment = paymentGet.execute(id);
        form.setPaymentId(payment.getId());

        // read only fields
        model.addAttribute("paymentId", payment.getId());
        model.addAttribute("invoiceId", payment.getInvoiceId());
        model.addAttribute("isPending", payment.getStatus() == PaymentStatus.PENDING);
        model.addAttribute("status", payment.getStatus().toString());
        model.addAttribute("merchant", payment.getMerchant());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("currency", payment.getCurrency());
        model.addAttribute("form", form);
        return "layout/main";
    }

    @PostMapping("/payments")
    public String processPayment(PaymentForm form, Model model) {
        if (form.getPaymentId() == null) {
            return "layout/main";
        }

        Long paymentId = form.getPaymentId();
        log.debug("Processing payment with id: {}", paymentId);

        // Process payment
        Payment payment = paymentSubmit.execute(
                paymentId,
                form.getCardNumber(),
                form.getCvv(),
                form.getCardPin()
        );

        // read only fields
        model.addAttribute("paymentId", payment.getId());
        model.addAttribute("invoiceId", payment.getInvoiceId());
        model.addAttribute("isPending", false);
        model.addAttribute("status", payment.getStatus().toString());
        model.addAttribute("merchant", payment.getMerchant());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("currency", payment.getCurrency());
        model.addAttribute("form", form);
        return "layout/main";
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