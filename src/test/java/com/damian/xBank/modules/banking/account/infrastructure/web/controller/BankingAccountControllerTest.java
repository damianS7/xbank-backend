package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BankingAccountControllerTest extends AbstractControllerTest {
    private Customer customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@demo.com")
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

        bankingAccount = BankingAccount
                .create(customer)
                .setAccountNumber("US0011111111222222223333")
                .setStatus(BankingAccountStatus.ACTIVE);

        customer.addBankingAccount(bankingAccount);
        bankingAccountRepository.save(bankingAccount);

    }

    @Test
    @DisplayName("should return all the customer banking accounts")
    void getAccounts_WhenValidRequest_ThenReturnBankingAccount() throws Exception {
        // given
        login(customer);

        // when
        MvcResult result = mockMvc
                .perform(get("/api/v1/banking/accounts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingAccountDto[] bankingAccountDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingAccountDto[].class
        );

        // then
        assertThat(bankingAccountDto[0])
                .isNotNull()
                .extracting(
                        BankingAccountDto::id,
                        BankingAccountDto::accountNumber,
                        BankingAccountDto::accountStatus
                ).containsExactly(
                        bankingAccount.getId(),
                        bankingAccount.getAccountNumber(),
                        bankingAccount.getStatus()
                );
    }

    @Test
    @DisplayName("should create banking account with valid request")
    void postAccounts_WhenValidRequest_ThenReturnCreatedAccount() throws Exception {
        // given
        login(customer);
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when
        MvcResult result = mockMvc
                .perform(post("/api/v1/banking/accounts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(201))
                .andReturn();

        BankingAccountDto bankingAccountDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingAccountDto.class
        );

        // then
        assertThat(bankingAccountDto).isNotNull();
        assertThat(bankingAccountDto.accountNumber()).isNotEmpty();
        assertThat(bankingAccountDto.accountCurrency()).isEqualTo(request.currency());
        assertThat(bankingAccountDto.balance()).isEqualTo(BigDecimal.ZERO);
        assertThat(bankingAccountDto.accountType()).isEqualTo(request.type());
    }

    @Test
    @DisplayName("should return 400 when request is invalid")
    void postAccounts_WhenAllFieldsMissing_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                null,
                null
        );

        // when
        mockMvc
                .perform(post("/api/v1/banking/accounts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    @DisplayName("should return 400 Bad Request when account type is missing")
    void postAccounts_WhenMissingAccountType_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                null,
                BankingAccountCurrency.EUR
        );

        // when & then
        mockMvc
                .perform(post("/api/v1/banking/accounts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 Bad Request when currency is missing")
    void postAccounts_WhenMissingCurrency_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                null
        );

        // when & then
        mockMvc
                .perform(post("/api/v1/banking/accounts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 401 Unauthorized when user is not authenticated")
    void postAccounts_WhenNotAuthenticated_ThenReturn401Unauthorized() throws Exception {
        // given
        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
                BankingAccountType.SAVINGS,
                BankingAccountCurrency.EUR
        );

        // when & then
        mockMvc
                .perform(post("/api/v1/banking/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return banking account closed")
    void postAccounts_WhenValidRequest_ThenReturnBankingAccountClosed() throws Exception {
        // given
        login(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/banking/accounts/{id}/close", bankingAccount.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingAccountDto bankingAccount = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingAccountDto.class
        );

        // then
        assertThat(bankingAccount).isNotNull();
        assertThat(bankingAccount.accountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
    }

    @Test
    @DisplayName("should return banking account with new alias")
    void postAccounts_WhenValidRequest_ThenReturnBankingAccountAliasUpdated() throws Exception {
        // given
        login(customer);

        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
                "account for savings"
        );

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/banking/accounts/{id}/alias", bankingAccount.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingAccountDto bankingAccount = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingAccountDto.class
        );

        // then
        assertThat(bankingAccount).isNotNull();
        assertThat(bankingAccount.alias()).isEqualTo(request.alias());
    }

    @Test
    @DisplayName("should return a new BankingCard")
    void postAccounts_WhenValidRequest_ThenReturnBankingCard() throws Exception {
        // given
        login(customer);

        BankingAccountCardRequest request = new BankingAccountCardRequest(
                BankingCardType.DEBIT
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/banking/accounts/{id}/cards", bankingAccount.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(201))
                .andReturn();

        BankingCardDto card = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(card)
                .isNotNull()
                .extracting(
                        BankingCardDto::bankingAccountId,
                        BankingCardDto::cardType
                )
                .containsExactly(
                        bankingAccount.getId(),
                        request.type()
                );
    }
}