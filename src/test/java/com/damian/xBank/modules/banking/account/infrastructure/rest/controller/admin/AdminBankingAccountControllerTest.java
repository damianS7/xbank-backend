package com.damian.xBank.modules.banking.account.infrastructure.rest.controller.admin;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.DepositBankingAccountRequest;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminBankingAccountControllerTest extends AbstractControllerTest {
    private User customer;
    private User admin;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withPassword(passwordEncoder.encode(RAW_PASSWORD))
            .build();

        userRepository.save(customer);

        admin = UserTestBuilder.builder()
            .withEmail("admin@demo.com")
            .withRole(UserRole.ADMIN)
            .withStatus(UserStatus.VERIFIED)
            .withPassword(passwordEncoder.encode(RAW_PASSWORD))
            .build();

        userRepository.save(admin);
    }

    @Test
    @DisplayName("should return a deposit transaction when request is valid")
    void postDeposit_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(admin);

        BigDecimal givenDepositAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        bankingAccountRepository.save(bankingAccount);

        DepositBankingAccountRequest request = new DepositBankingAccountRequest(
            "DAMIAN MG",
            BigDecimal.valueOf(100)
        );

        // when
        MvcResult result = mockMvc
            .perform(post("/api/v1/admin/banking/accounts/{id}/deposit", bankingAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(201))
            .andReturn();

        // then
        BankingTransactionResult transaction = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingTransactionResult.class
        );

        BankingAccount updatedBankingAccount = bankingAccountRepository.findById(bankingAccount.getId()).get();

        // then
        assertThat(transaction).isNotNull();
        assertEquals(
            updatedBankingAccount.getBalance(),
            bankingAccount.getBalance().add(givenDepositAmount).setScale(2)
        );
        assertEquals(BankingTransactionType.DEPOSIT, transaction.type());
        assertEquals(transaction.amount(), givenDepositAmount);
    }

    @Test
    @DisplayName("should return a 403 when user is not admin")
    void postDeposit_WhenUserNotAdmin_Returns403Forbidden() throws Exception {
        // given
        login(customer);

        BankingAccount bankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        bankingAccountRepository.save(bankingAccount);

        DepositBankingAccountRequest request = new DepositBankingAccountRequest(
            "DAMIAN MG",
            BigDecimal.valueOf(100)
        );

        // when
        mockMvc
            .perform(post("/api/v1/admin/banking/accounts/{id}/deposit", bankingAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(403));
    }
}