package com.damian.xBank.modules.payment.checkout.infrastructure;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidCvvException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidPinException;
import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutGet;
import com.damian.xBank.modules.payment.checkout.application.usecase.PaymentCheckoutSubmit;
import com.damian.xBank.modules.payment.checkout.domain.PaymentCheckoutForm;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
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
    private final PaymentCheckoutSubmit paymentGatewayConfirm;

    public PaymentCheckoutController(
            PaymentCheckoutGet paymentCheckoutGet,
            PaymentCheckoutSubmit paymentCheckoutSubmit
    ) {
        this.paymentCheckoutGet = paymentCheckoutGet;
        this.paymentGatewayConfirm = paymentCheckoutSubmit;
    }

    @GetMapping("/payments/{id}/checkout")
    public String showPaymentForm(
            @PathVariable Long id,
            Model model
    ) {
        PaymentCheckoutForm form = new PaymentCheckoutForm();

        // Get payment
        PaymentIntent paymentIntent = paymentCheckoutGet.execute(id);
        form.setPaymentId(paymentIntent.getId());

        // read only fields
        model.addAttribute("paymentId", paymentIntent.getId());
        model.addAttribute("invoiceId", paymentIntent.getMerchantName());
        model.addAttribute("isPending", paymentIntent.getStatus() == PaymentIntentStatus.PENDING);
        model.addAttribute("status", paymentIntent.getStatus().toString());
        model.addAttribute("merchant", paymentIntent.getMerchantName());
        model.addAttribute("amount", paymentIntent.getAmount());
        model.addAttribute("currency", paymentIntent.getCurrency());
        model.addAttribute("form", form);
        return "layout/main";
    }

    @PostMapping("/payments/{id}/checkout")
    public String processPayment(PaymentCheckoutForm form, Model model) {
        if (form.getPaymentId() == null) {
            return "layout/main";
        }

        Long paymentId = form.getPaymentId();
        log.debug("Processing payment with id: {}", paymentId);

        // Process payment
        PaymentIntent paymentIntent = paymentGatewayConfirm.execute(
                new PaymentCheckoutSubmitRequest(
                        paymentId,
                        form.getCardNumber(),
                        form.getCvv(),
                        form.getCardPin(),
                        ""
                )
        );

        // read only fields
        model.addAttribute("paymentId", paymentIntent.getId());
        model.addAttribute("isPending", false);
        model.addAttribute("status", paymentIntent.getStatus().toString());
        model.addAttribute("merchant", paymentIntent.getMerchantName());
        model.addAttribute("amount", paymentIntent.getAmount());
        model.addAttribute("currency", paymentIntent.getCurrency());
        model.addAttribute("form", form);
        return "layout/main";
        // TODO return to the store? with payment data
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