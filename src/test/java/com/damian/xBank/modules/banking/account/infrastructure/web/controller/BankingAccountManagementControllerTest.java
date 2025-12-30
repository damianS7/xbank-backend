package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingAccountManagementControllerTest extends AbstractControllerTest {

    private Customer customer;
    private Customer admin;

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

        admin = Customer.create()
                        .setEmail("admin@demo.com")
                        .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                        .setFirstName("David")
                        .setLastName("Brow")
                        .setBirthdate(LocalDate.now())
                        .setPhotoPath("avatar.jpg")
                        .setPhone("123 123 123")
                        .setPostalCode("01003")
                        .setAddress("Fake ave")
                        .setCountry("US")
                        .setRole(UserAccountRole.ADMIN)
                        .setGender(CustomerGender.MALE);
        admin.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(admin);
    }

    @Test
    @DisplayName("Should close your own account")
    void shouldCloseAccount() throws Exception {
        // given
        login(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountNumber("US0011111111222222223333");
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        bankingAccountRepository.save(givenBankingAccount);

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/banking/accounts/{id}/close", givenBankingAccount.getId())
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
    @DisplayName("Should set an alias to your own banking account")
    void shouldSetAlias() throws Exception {
        // given
        login(customer);
        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
                "account for savings"
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountNumber("US0011111111222222223333");
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        bankingAccountRepository.save(givenBankingAccount);

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/banking/accounts/{id}/alias", givenBankingAccount.getId())
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
    @DisplayName("Should request a BankingCard")
    void shouldRequestCard() throws Exception {
        // given
        login(customer);

        BankingAccount bankingAccount = new BankingAccount(customer);
        bankingAccount.setAccountNumber("ES1234567890123456789012");
        bankingAccount.setAccountType(BankingAccountType.SAVINGS);
        bankingAccount.setCurrency(BankingAccountCurrency.EUR);
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);
        bankingAccount.setBalance(BigDecimal.valueOf(1000));
        bankingAccountRepository.save(bankingAccount);

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
        assertThat(card).isNotNull();
        assertThat(card.cardType()).isEqualTo(request.type());
    }
}