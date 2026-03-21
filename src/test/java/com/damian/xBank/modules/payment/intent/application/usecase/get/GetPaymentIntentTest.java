package com.damian.xBank.modules.payment.intent.application.usecase.get;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class GetPaymentIntentTest extends AbstractServiceTest {

    @Mock
    private PaymentIntentRepository paymentIntentRepository;

    @InjectMocks
    private GetPaymentIntent getPaymentIntent;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();
    }

    @Test
    @DisplayName("returns payment intent")
    void getPaymentIntent_ReturnsPaymentIntent() {
        // given
        PaymentIntent paymentIntent = PaymentIntent.create(
            customer,
            BigDecimal.valueOf(100),
            BankingAccountCurrency.EUR
        );

        GetPaymentIntentQuery query = new GetPaymentIntentQuery(1L);

        // when
        when(paymentIntentRepository.findById(anyLong()))
            .thenReturn(Optional.of(paymentIntent));

        GetPaymentIntentResult result = getPaymentIntent.execute(query);

        // then
        assertThat(result).isNotNull();
    }
}