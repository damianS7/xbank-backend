package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
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

public class BankingAccountOperationControllerTest extends AbstractControllerTest {

    private Customer customerA;
    private Customer customerB;
    private Customer admin;

    @BeforeEach
    void setUp() {
        customerA = Customer.create()
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
        customerA.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customerA);

        customerB = Customer.create()
                            .setEmail("customerB@demo.com")
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
        customerB.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customerB);

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
    @DisplayName("Should transfer to another customer")
    void shouldTransferToAnotherCustomer() throws Exception {
        // given
        login(customerA);

        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccountA = new BankingAccount(customerA);
        bankingAccountA.setAccountNumber("ES1234567890123456789012");
        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
        bankingAccountRepository.save(bankingAccountA);

        BankingAccount bankingAccountB = new BankingAccount(customerB);
        bankingAccountB.setAccountNumber("DE1234567890123456789012");
        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountB.setBalance(BigDecimal.valueOf(200));
        bankingAccountRepository.save(bankingAccountB);

        BankingAccountTransferRequest request = new BankingAccountTransferRequest(
                bankingAccountB.getAccountNumber(),
                "Enjoy!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(post(
                        "/api/v1/banking/accounts/" + bankingAccountA.getId() + "/transfer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(201))
                .andReturn();

        BankingTransactionDto transaction = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BankingTransactionDto.class
        );

        BankingAccount updatedBankingAccountA = bankingAccountRepository.findById(bankingAccountA.getId()).get();

        // then
        assertThat(transaction).isNotNull();
        assertEquals(
                updatedBankingAccountA.getBalance(),
                bankingAccountA.getBalance().subtract(givenTransferAmount).setScale(2)
        );
        assertEquals(BankingTransactionType.TRANSFER_TO, transaction.type());
        assertEquals(transaction.amount(), givenTransferAmount);
        assertEquals("Enjoy!", transaction.description());
        assertEquals(transaction.accountId(), bankingAccountA.getId());
    }

    @Test
    @DisplayName("Should not transfer to same account number")
    void shouldNotTransferToSameAccount() throws Exception {
        // given
        login(customerA);

        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccountA = new BankingAccount(customerA);
        bankingAccountA.setAccountNumber("ES1234567890123456789012");
        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
        bankingAccountRepository.save(bankingAccountA);

        BankingAccountTransferRequest request = new BankingAccountTransferRequest(
                bankingAccountA.getAccountNumber(),
                "Enjoy!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        // when
        mockMvc
                .perform(post(
                        "/api/v1/banking/accounts/" + bankingAccountA.getId() + "/transfer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(409))
                .andReturn();
    }

    @Test
    @DisplayName("Should not transfer when account is closed")
    void shouldNotTransferWhenAccountIsClosed() throws Exception {
        // given
        login(customerA);
        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccountA = new BankingAccount(customerA);
        bankingAccountA.setAccountNumber("ES1234567890123456789012");
        bankingAccountA.setAccountStatus(BankingAccountStatus.CLOSED);
        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
        bankingAccountRepository.save(bankingAccountA);

        BankingAccount bankingAccountB = new BankingAccount(customerB);
        bankingAccountB.setAccountNumber("DE1234567890123456789012");
        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountB.setBalance(BigDecimal.valueOf(200));
        bankingAccountRepository.save(bankingAccountB);

        BankingAccountTransferRequest request = new BankingAccountTransferRequest(
                bankingAccountB.getAccountNumber(),
                "Enjoy!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        mockMvc.perform(post("/api/v1/banking/accounts/{id}/transfer", bankingAccountA.getId())
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().is(409));
    }

    @Test
    @DisplayName("Should not transfer when account is suspended")
    void shouldNotTransferWhenAccountIsSuspended() throws Exception {
        // given
        login(customerA);
        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccountA = new BankingAccount(customerA);
        bankingAccountA.setAccountNumber("ES1234567890123456789012");
        bankingAccountA.setAccountStatus(BankingAccountStatus.SUSPENDED);
        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
        bankingAccountRepository.save(bankingAccountA);

        BankingAccount bankingAccountB = new BankingAccount(customerB);
        bankingAccountB.setAccountNumber("DE1234567890123456789012");
        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountB.setBalance(BigDecimal.valueOf(200));
        bankingAccountRepository.save(bankingAccountB);

        BankingAccountTransferRequest request = new BankingAccountTransferRequest(
                bankingAccountB.getAccountNumber(),
                "Enjoy!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        mockMvc.perform(post("/api/v1/banking/accounts/{id}/transfer", bankingAccountA.getId())
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().is(403));
    }


    @Test
    @DisplayName("Should not transfer to when insufficient funds")
    void shouldTransferToWhenInsufficientFunds() throws Exception {
        // given
        login(customerA);

        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingAccount bankingAccountA = new BankingAccount(customerA);
        bankingAccountA.setAccountNumber("ES1234567890123456789012");
        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountA.setBalance(BigDecimal.valueOf(0));
        bankingAccountRepository.save(bankingAccountA);

        BankingAccount bankingAccountB = new BankingAccount(customerB);
        bankingAccountB.setAccountNumber("DE1234567890123456789012");
        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
        bankingAccountB.setBalance(BigDecimal.valueOf(200));
        bankingAccountRepository.save(bankingAccountB);

        BankingAccountTransferRequest request = new BankingAccountTransferRequest(
                bankingAccountB.getAccountNumber(),
                "Enjoy!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(post(
                        "/api/v1/banking/accounts/" + bankingAccountA.getId() + "/transfer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(409))
                .andReturn();
    }
}