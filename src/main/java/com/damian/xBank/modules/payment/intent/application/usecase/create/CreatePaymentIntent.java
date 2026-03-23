package com.damian.xBank.modules.payment.intent.application.usecase.create;


import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.user.merchant.domain.Merchant;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el merchant (user) crea un payment intent.
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
     *
     * @param command
     * @return Un payment intent en estado PENDING es devuelto al merchant.
     */
    @Transactional
    public CreatePaymentIntentResult execute(CreatePaymentIntentCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();
        final Merchant currentMerchant = currentUser.getMerchant();

        PaymentIntent paymentIntent = PaymentIntent.create(
            currentMerchant,
            command.orderId(),
            command.amount(),
            BankingAccountCurrency.valueOf(command.currency()),
            command.description()
        );

        PaymentIntent savedPaymentIntent = paymentIntentRepository.save(paymentIntent);
        return CreatePaymentIntentResult.from(savedPaymentIntent);
    }
}