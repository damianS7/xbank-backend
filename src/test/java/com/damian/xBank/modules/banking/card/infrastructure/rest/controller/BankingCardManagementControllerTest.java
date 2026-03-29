package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.LockBankingCardRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.SetBankingCardDailyLimitRequest;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.SetBankingCardPinRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestBuilder;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingCardManagementControllerTest extends AbstractControllerTest {

    private User customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer().build();
        userRepository.save(customer);

        customerBankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
        bankingAccountRepository.save(customerBankingAccount);

        customerBankingCard = BankingCardTestFactory.aDebitCard(customerBankingAccount)
            .build();
        bankingCardRepository.save(customerBankingCard);
    }

    @Test
    @DisplayName("should return 200 OK when update card pin")
    void postPin_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

        SetBankingCardPinRequest request = new SetBankingCardPinRequest("7777", RAW_PASSWORD);

        // when
        MvcResult result = mockMvc
            .perform(patch("/api/v1/banking/cards/{id}/pin", customerBankingCard.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        BankingCardResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingCardResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.cardPIN()).isEqualTo(request.pin());
    }

    @Test
    @DisplayName("should return 200 OK when update card Daily Limit")
    void postDailyLimit_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

        SetBankingCardDailyLimitRequest request = new SetBankingCardDailyLimitRequest(
            BigDecimal.valueOf(7777),
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(
                patch("/api/v1/banking/cards/{id}/daily-limit", customerBankingCard.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        BankingCardResult cardResponseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingCardResult.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.dailyLimit()).isEqualTo(request.dailyLimit());
    }

    @Test
    @DisplayName("should return 200 OK when lock card")
    void postLock_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

        LockBankingCardRequest request = new LockBankingCardRequest(
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(
                patch("/api/v1/banking/cards/{id}/lock", customerBankingCard.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        BankingCardResult cardResponseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingCardResult.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.cardStatus()).isEqualTo(BankingCardStatus.LOCKED);
    }

    @Test
    @DisplayName("should return 200 OK when unlock card")
    void postUnlock_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

        BankingCard customerBankingCard = BankingCardTestBuilder.builder()
            .withOwnerAccount(customerBankingAccount)
            .withCardNumber("1234123412341234")
            .withStatus(BankingCardStatus.LOCKED)
            .withCVV("123")
            .withPIN("1234")
            .build();

        bankingCardRepository.save(customerBankingCard);

        LockBankingCardRequest request = new LockBankingCardRequest(
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(
                patch("/api/v1/banking/cards/{id}/unlock", customerBankingCard.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        BankingCardResult cardResponseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingCardResult.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.cardStatus()).isEqualTo(BankingCardStatus.ACTIVE);
    }
}