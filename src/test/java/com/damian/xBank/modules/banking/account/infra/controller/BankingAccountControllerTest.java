package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCreateRequest;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BankingAccountControllerTest extends AbstractIntegrationTest {
    private Customer customer;

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

    }

    @Test
    @DisplayName("Should create a banking account")
    void shouldCreateBankingAccount() throws Exception {
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

        BankingAccountDto bankingAccount = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingAccountDto.class
        );

        // then
        assertThat(bankingAccount).isNotNull();
        assertThat(bankingAccount.accountNumber()).isNotEmpty();
        assertThat(bankingAccount.accountCurrency()).isEqualTo(request.currency());
        assertThat(bankingAccount.balance()).isEqualTo(BigDecimal.ZERO);
        assertThat(bankingAccount.accountType()).isEqualTo(request.type());
    }

    @Test
    @DisplayName("Should not create banking account when request is invalid")
    void shouldNotCreateBankingAccountWhenInvalidRequest() throws Exception {
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
}