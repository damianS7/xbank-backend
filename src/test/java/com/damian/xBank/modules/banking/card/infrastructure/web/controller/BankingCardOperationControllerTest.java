package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.CaptureCardPaymentRequest;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
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
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

        customerBankingAccount = BankingAccount
                .create(customer)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");

        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setExpiredDate(LocalDate.now().plusYears(1))
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        customerBankingAccount.addBankingCard(customerBankingCard);
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("should return 201 CREATED when withdraw from card")
    void postWithdraw_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(customer);

        BankingCardWithdrawRequest request = new BankingCardWithdrawRequest(
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

        BankingTransactionDto transactionResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingTransactionDto.class
        );

        // then
        assertThat(transactionResponseDto).isNotNull();
        assertThat(transactionResponseDto.amount()).isEqualTo(request.amount());
        assertThat(transactionResponseDto.status()).isEqualTo(BankingTransactionStatus.COMPLETED);
        assertThat(transactionResponseDto.balanceBefore())
                .isEqualByComparingTo(customerBankingAccount.getBalance());
        assertThat(transactionResponseDto.balanceAfter())
                .isEqualByComparingTo(customerBankingAccount.getBalance().subtract(request.amount()));
    }

    @Test
    @DisplayName("should return 200 OK when authorize payment")
    void authorizePayment_WhenValidRequest_Returns200OK() throws Exception {
        // given
        AuthorizeCardPaymentRequest request = new AuthorizeCardPaymentRequest(
                "Amazon.com",
                "John",
                customerBankingCard.getCardNumber(),
                customerBankingCard.getExpiredDate().getMonthValue(),
                customerBankingCard.getExpiredDate().getYear(),
                customerBankingCard.getCardCvv(),
                BigDecimal.valueOf(100)
        );

        // when
        when(paymentNetworkGateway.authorizePayment(
                anyString(),
                anyString(),
                anyString(),
                any(BigDecimal.class),
                anyString()
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

        BankingTransaction transaction = BankingTransaction.create(
                BankingTransactionType.CARD_CHARGE,
                customerBankingCard,
                BigDecimal.valueOf(100)
        );
        transaction.setStatus(BankingTransactionStatus.AUTHORIZED);
        bankingTransactionRepository.save(transaction);

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

        BankingTransactionDto transactionResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingTransactionDto.class
        );

        System.out.println(initialBalance);
        System.out.println(transaction.getBalanceBefore());
        System.out.println(transaction.getBalanceAfter());

        System.out.println(transactionResponseDto.balanceBefore());
        System.out.println(transactionResponseDto.balanceAfter());
        // then
        // TODO check this
        assertThat(transactionResponseDto).isNotNull();
        assertThat(transactionResponseDto.status()).isEqualTo(BankingTransactionStatus.CAPTURED);
        assertThat(transactionResponseDto.balanceBefore())
                .isEqualByComparingTo(initialBalance);
        assertThat(transactionResponseDto.balanceAfter())
                .isEqualByComparingTo(initialBalance.subtract(transaction.getAmount()));
    }
}