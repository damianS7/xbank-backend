package com.damian.xBank.modules.payment.intent.application.usecase;


import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.application.dto.request.CreatePaymentIntentRequest;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used by the merchant to create a pending payment intent.
 * <p>
 * Pending payment its returned to the merchant so they can redirect the
 * user to the payment gateway.
 */
@Service
public class CreatePaymentIntent {
    private final PaymentIntentRepository paymentIntentRepository;
    private final AuthenticationContext authenticationContext;

    public CreatePaymentIntent(
            PaymentIntentRepository paymentIntentRepository,
            AuthenticationContext authenticationContext
    ) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * It creates a payment intent for current merchant
     *
     * @param request
     * @return Created payment intent
     */
    @Transactional
    public PaymentIntent execute(CreatePaymentIntentRequest request) {
        // Current user
        final User currentMerchant = authenticationContext.getCurrentUser();

        PaymentIntent paymentIntent = new PaymentIntent(
                currentMerchant,
                request.amount(),
                BankingAccountCurrency.valueOf(request.currency())
        );

        return paymentIntentRepository.save(paymentIntent);
    }
}