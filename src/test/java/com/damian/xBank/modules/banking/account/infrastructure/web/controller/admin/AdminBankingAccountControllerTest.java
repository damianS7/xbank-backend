package com.damian.xBank.modules.banking.account.infrastructure.web.controller.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminBankingAccountControllerTest extends AbstractControllerTest {
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
    @DisplayName("should return a deposit transaction when request is valid")
    void postDeposit_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(admin);

        BigDecimal givenDepositAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccount = new BankingAccount(customer);
        bankingAccount.setAccountNumber("ES1234567890123456789012");
        bankingAccount.setType(BankingAccountType.SAVINGS);
        bankingAccount.setCurrency(BankingAccountCurrency.EUR);
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);
        bankingAccount.setBalance(BigDecimal.valueOf(1000));
        bankingAccountRepository.save(bankingAccount);

        BankingAccountDepositRequest request = new BankingAccountDepositRequest(
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
        BankingTransactionDto transaction = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingTransactionDto.class
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

        BankingAccount bankingAccount = new BankingAccount(customer);
        bankingAccount.setAccountNumber("ES1234567890123456789012");
        bankingAccount.setType(BankingAccountType.SAVINGS);
        bankingAccount.setCurrency(BankingAccountCurrency.EUR);
        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);
        bankingAccount.setBalance(BigDecimal.valueOf(1000));
        bankingAccountRepository.save(bankingAccount);

        BankingAccountDepositRequest request = new BankingAccountDepositRequest(
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