package com.damian.xBank.modules.banking.card.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.shared.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingCardManagementControllerTest extends AbstractControllerTest {

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@demo.com")
                           .setRole(UserAccountRole.CUSTOMER)
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setFirstName("David")
                           .setLastName("Brow")
                           .setBirthdate(LocalDate.now())
                           .setPhotoPath("avatar.jpg")
                           .setPhone("123 123 123")
                           .setPostalCode("01003")
                           .setAddress("Fake ave")
                           .setCountry("US")
                           .setGender(CustomerGender.MALE);
        customer.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customer);

        customerBankingAccount = BankingAccount
                .create(customer)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setStatus(BankingCardStatus.ACTIVE)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        customerBankingAccount.addBankingCard(customerBankingCard);
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("Should update card pin")
    void shouldUpdateCardPin() throws Exception {
        // given
        login(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/banking/cards/{id}/pin", customerBankingCard.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingCardDto cardResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.cardPIN()).isEqualTo(request.pin());
    }

    @Test
    @DisplayName("Should update card Daily Limit")
    void shouldUpdateCardDailyLimit() throws Exception {
        // given
        login(customer);

        BankingCardUpdateDailyLimitRequest request = new BankingCardUpdateDailyLimitRequest(
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

        BankingCardDto cardResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.dailyLimit()).isEqualTo(request.dailyLimit());
    }

    @Test
    @DisplayName("Should update card status to locked")
    void shouldLockCard() throws Exception {
        // given
        login(customer);

        BankingCardUpdateLockRequest request = new BankingCardUpdateLockRequest(
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/banking/cards/{id}/lock-status", customerBankingCard.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingCardDto cardResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.cardStatus()).isEqualTo(BankingCardStatus.LOCKED);
    }

    @Test
    @DisplayName("Should update card status to active")
    void shouldUnlockCard() throws Exception {
        // given
        login(customer);

        customerBankingCard.setStatus(BankingCardStatus.LOCKED);
        bankingCardRepository.save(customerBankingCard);

        BankingCardUpdateLockRequest request = new BankingCardUpdateLockRequest(
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/banking/cards/{id}/lock-status", customerBankingCard.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingCardDto cardResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(cardResponseDto).isNotNull();
        assertThat(cardResponseDto.cardStatus()).isEqualTo(BankingCardStatus.ACTIVE);
    }
}