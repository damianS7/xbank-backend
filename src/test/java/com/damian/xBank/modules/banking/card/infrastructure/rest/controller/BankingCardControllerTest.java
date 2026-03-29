package com.damian.xBank.modules.banking.card.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.rest.request.AuthorizeCardPaymentRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingCardControllerTest extends AbstractControllerTest {
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
    @DisplayName("should return the customer banking cards")
    void getCards_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

        // when
        // then
        mockMvc.perform(get("/api/v1/banking/cards")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andDo(print())
            .andExpect(status().is(200));
    }

    @Test
    @DisplayName("should authorize card")
    void authorizeCard_WhenValidRequest_Returns200OK() throws Exception {
        // given
        login(customer);

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
        // then
        mockMvc.perform(post("/api/v1/banking/cards/authorize")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonHelper.toJson(request)))
            .andDo(print())
            .andExpect(status().is(200));
    }
}