package com.damian.xBank.modules.payment.checkout.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
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
import static org.mockito.Mockito.*;

public class PaymentCheckoutSubmitTest extends AbstractServiceTest {

    @Mock
    private PaymentIntentRepository paymentIntentRepository;

    @Mock
    private PaymentNetworkGateway paymentNetworkGateway;

    @InjectMocks
    private PaymentCheckoutSubmit checkoutSubmit;

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

        PaymentCheckoutSubmitRequest request = new PaymentCheckoutSubmitRequest(
                0L,
                "John Doe",
                "1234123412341234",
                "123",
                "1234",
                1,
                2026,
                "localhost"
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

        PaymentIntent result = checkoutSubmit.execute(request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        PaymentIntent::getAmount,
                        PaymentIntent::getCurrency,
                        PaymentIntent::getStatus
                ).containsExactly(
                        paymentIntent.getAmount(),
                        paymentIntent.getCurrency(),
                        PaymentIntentStatus.AUTHORIZED
                );

        verify(paymentIntentRepository, times(1)).save(any(PaymentIntent.class));
    }
}