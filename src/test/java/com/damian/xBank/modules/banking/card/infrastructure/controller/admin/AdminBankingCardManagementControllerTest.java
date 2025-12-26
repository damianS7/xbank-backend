package com.damian.xBank.modules.banking.card.infrastructure.controller.admin;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
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

public class AdminBankingCardManagementControllerTest extends AbstractControllerTest {
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
                .setAccountType(BankingAccountType.SAVINGS)
                .setStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setBankingAccount(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        customerBankingAccount.addBankingCard(customerBankingCard);
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("Should disable a card when logged as admin")
    void shouldDisableCardWhenAdmin() throws Exception {
        // given
        customer.setRole(UserAccountRole.ADMIN);
        customerRepository.save(customer);

        login(customer);

        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
                BankingCardStatus.DISABLED
        );

        // when
        MvcResult result = mockMvc
                .perform(patch("/api/v1/admin/banking/cards/{id}/status", customerBankingCard.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        BankingCardDto card = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingCardDto.class
        );

        // then
        assertThat(card).isNotNull();
        assertThat(card.cardStatus()).isEqualTo(BankingCardStatus.DISABLED);
    }

    @Test
    @DisplayName("Should fail to disable a card when not admin")
    void shouldFailToDisableCardWhenNotAdmin() throws Exception {
        // given
        customer.setRole(UserAccountRole.CUSTOMER);
        customerRepository.save(customer);

        login(customer);

        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
                BankingCardStatus.DISABLED
        );

        // when
        mockMvc
                .perform(patch("/api/v1/admin/banking/cards/{id}/status", customerBankingCard.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(403));
    }
}