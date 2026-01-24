package com.damian.xBank.modules.payment.intent.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.application.dto.request.CreatePaymentIntentRequest;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreatePaymentIntentTest extends AbstractServiceTest {

    @Mock
    private PaymentIntentRepository paymentIntentRepository;

    @InjectMocks
    private CreatePaymentIntent createPaymentIntent;

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
    @DisplayName("returns created payment intent")
    void createPaymentIntent_WhenValidRequest_ReturnsCreatedPaymentIntent() {
        // given
        setUpContext(customer);

        PaymentIntent paymentIntent = new PaymentIntent(
                customer,
                BigDecimal.valueOf(100),
                BankingAccountCurrency.EUR
        );

        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                paymentIntent.getAmount(),
                paymentIntent.getCurrency().toString()
        );

        // when
        when(paymentIntentRepository.save(any(PaymentIntent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentIntent result = createPaymentIntent.execute(request);

        // then
        assertThat(result)
                .isNotNull();
        verify(paymentIntentRepository, times(1)).save(any(PaymentIntent.class));
    }
}