package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.usecase.capture.CaptureCardPaymentResult;
import com.damian.xBank.modules.banking.card.application.usecase.withdraw.WithdrawFromATMResult;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardTestBuilder;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.CaptureCardPaymentRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.WithdrawFromATMRequest;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionTestBuilder;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.payment.checkout.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingCardOperationControllerTest extends AbstractControllerTest {
    @MockitoBean
    private PaymentNetworkGateway paymentNetworkGateway;

    private User customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withEmail("customer@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withPassword(passwordEncoder.encode(RAW_PASSWORD))
            .build();

        userRepository.save(customer);

        customerBankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
        bankingAccountRepository.save(customerBankingAccount);

        customerBankingCard = BankingCardTestBuilder.builder()
            .withOwnerAccount(customerBankingAccount)
            .withCardNumber("1234123412341234")
            .withStatus(BankingCardStatus.ACTIVE)
            .withCVV("123")
            .withPIN("1234")
            .build();

        bankingCardRepository.save(customerBankingCard);
    }

    @Test
    @DisplayName("should return 201 CREATED when withdraw from card")
    void postWithdraw_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(customer);
        BigDecimal initialBalance = customerBankingAccount.getBalance();

        WithdrawFromATMRequest request = new WithdrawFromATMRequest(
            BigDecimal.valueOf(100),
            customerBankingCard.getCardPin()
        );

        // when
        // then
        MvcResult result = mockMvc.perform(post("/api/v1/banking/cards/{id}/withdraw", customerBankingCard.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(201))
            .andReturn();

        WithdrawFromATMResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            WithdrawFromATMResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualTo(request.amount());
        assertThat(response.status()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(response.balanceBefore()).isEqualByComparingTo(initialBalance);
        assertThat(response.balanceAfter())
            .isEqualByComparingTo(initialBalance.subtract(request.amount()));
    }

    @Test
    @DisplayName("should return 200 OK when authorize payment")
    void authorize_WhenValidRequest_Returns200OK() throws Exception {
        // given
        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
            "Amazon.com",
            "John",
            customerBankingCard.getCardNumber(),
            customerBankingCard.getExpiration().getMonth(),
            customerBankingCard.getExpiration().getYear(),
            customerBankingCard.getCardCvv(),
            BigDecimal.valueOf(100)
        );

        // when
        when(paymentNetworkGateway.authorizePayment(
            any(PaymentAuthorizationRequest.class)
        )).thenReturn(new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            "authorizationId",
            null
        ));

        // then
        MvcResult result = mockMvc.perform(post("/api/v1/banking/cards/authorize", customerBankingCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();
    }

    @Test
    @DisplayName("should return 200 OK when capture payment from card")
    void capturePayment_WhenValidRequest_Returns200Created() throws Exception {
        // given
        login(customer);

        BigDecimal initialBalance = customerBankingAccount.getBalance();

        BankingTransaction transaction = BankingTransactionTestBuilder.builder()
            .withCard(customerBankingCard)
            .withAmount(BigDecimal.valueOf(100))
            .withStatus(BankingTransactionStatus.PENDING)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withDescription("Deposit transaction")
            .build();

        transactionRepository.save(transaction);

        CaptureCardPaymentRequest request = new CaptureCardPaymentRequest(
            transaction.getId()
        );

        // when
        // then
        MvcResult result = mockMvc.perform(post("/api/v1/banking/cards/capture", customerBankingCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        CaptureCardPaymentResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CaptureCardPaymentResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(response.balanceBefore())
            .isEqualByComparingTo(initialBalance);
        assertThat(response.balanceAfter())
            .isEqualByComparingTo(initialBalance.subtract(transaction.getAmount()));
    }
}