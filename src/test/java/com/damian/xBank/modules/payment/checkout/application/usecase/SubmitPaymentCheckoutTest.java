package com.damian.xBank.modules.payment.checkout.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.checkout.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.checkout.application.usecase.submit.SubmitPaymentCheckout;
import com.damian.xBank.modules.payment.checkout.application.usecase.submit.SubmitPaymentCheckoutCommand;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubmitPaymentCheckoutTest extends AbstractServiceTest {

    @Mock
    private PaymentIntentRepository paymentIntentRepository;

    @Mock
    private PaymentNetworkGateway paymentNetworkGateway;

    @InjectMocks
    private SubmitPaymentCheckout submitPaymentCheckout;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();
    }

    @Test
    @DisplayName("returns authorized payment intent")
    void checkoutSubmit_WhenValidRequest_ReturnsAuthorizedPaymentIntent() {
        // given
        PaymentIntent paymentIntent = new PaymentIntent(
            customer,
            BigDecimal.valueOf(100),
            BankingAccountCurrency.EUR
        );

        SubmitPaymentCheckoutCommand command = new SubmitPaymentCheckoutCommand(
            0L,
            "John Doe",
            "1234123412341234",
            "123",
            "1234",
            1,
            2026
        );

        PaymentAuthorizationResponse response = new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            "1234",
            ""
        );

        // when
        when(paymentIntentRepository.findById(anyLong())).thenReturn(Optional.of(paymentIntent));

        when(paymentNetworkGateway.authorizePayment(
            any(PaymentAuthorizationRequest.class)
        )).thenReturn(response);

        when(paymentIntentRepository.save(any(PaymentIntent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        submitPaymentCheckout.execute(command);

        // then
        assertThat(paymentIntent.getStatus()).isEqualTo(PaymentIntentStatus.AUTHORIZED);

        verify(paymentIntentRepository, times(1)).save(any(PaymentIntent.class));
    }
}