package com.damian.xBank.modules.banking.transaction.infrastructure.controller;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.shared.AbstractControllerTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingTransactionControllerTest extends AbstractControllerTest {
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
                .create()
                .setOwner(customer)
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setAssociatedBankingAccount(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        customerBankingAccount.addTransaction(
                BankingTransaction
                        .create()
                        .setBankingAccount(customerBankingAccount)
                        .setBankingCard(customerBankingCard)
                        .setDescription("Amazon.com")
                        .setBalanceBefore(BigDecimal.valueOf(1000))
                        .setBalanceAfter(BigDecimal.valueOf(900))
                        .setStatus(BankingTransactionStatus.PENDING)
                        .setAmount(BigDecimal.valueOf(100))
                        .setType(BankingTransactionType.CARD_CHARGE)
        );

        customerBankingAccount.addTransaction(
                BankingTransaction
                        .create()
                        .setBankingAccount(customerBankingAccount)
                        .setBankingCard(customerBankingCard)
                        .setDescription("Netflix.com")
                        .setBalanceBefore(BigDecimal.valueOf(900))
                        .setBalanceAfter(BigDecimal.valueOf(870))
                        .setStatus(BankingTransactionStatus.COMPLETED)
                        .setAmount(BigDecimal.valueOf(30))
                        .setType(BankingTransactionType.CARD_CHARGE)
        );

        customerBankingAccount.addTransaction(
                BankingTransaction
                        .create()
                        .setBankingAccount(customerBankingAccount)
                        .setBankingCard(customerBankingCard)
                        .setDescription("HBO.com")
                        .setBalanceBefore(BigDecimal.valueOf(870))
                        .setBalanceAfter(BigDecimal.valueOf(850))
                        .setStatus(BankingTransactionStatus.PENDING)
                        .setAmount(BigDecimal.valueOf(20))
                        .setType(BankingTransactionType.CARD_CHARGE)
        );

        customerBankingAccount.addBankingCard(customerBankingCard);
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("Should get transaction")
    void shouldGetTransaction() throws Exception {
        // given
        login(customer);

        BankingTransaction transaction = BankingTransaction
                .create()
                .setBankingAccount(customerBankingAccount)
                .setBankingCard(customerBankingCard)
                .setDescription("Amazon.com")
                .setBalanceBefore(BigDecimal.valueOf(100))
                .setBalanceAfter(BigDecimal.valueOf(0))
                .setStatus(BankingTransactionStatus.COMPLETED)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.CARD_CHARGE);

        customerBankingAccount.addTransaction(transaction);
        transactionRepository.save(transaction);

        // when
        // then
        mockMvc.perform(get("/api/v1/banking/transactions/{id}", transaction.getId())
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Should get pending transactions")
    void shouldGetPendingTransactions() throws Exception {
        // given
        login(customer);
        Customer anotherCustomer = Customer.create()
                                           .setEmail("customer2@demo.com")
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
        customerRepository.save(anotherCustomer);

        BankingAccount anotherCustomerBankingAccount = BankingAccount
                .create()
                .setOwner(anotherCustomer)
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");

        anotherCustomerBankingAccount.addTransaction(
                BankingTransaction
                        .create()
                        .setBankingAccount(anotherCustomerBankingAccount)
                        .setDescription("Amazon.us")
                        .setBalanceBefore(BigDecimal.valueOf(1000))
                        .setBalanceAfter(BigDecimal.valueOf(900))
                        .setStatus(BankingTransactionStatus.PENDING)
                        .setAmount(BigDecimal.valueOf(100))
                        .setType(BankingTransactionType.TRANSFER_TO)
        );

        anotherCustomerBankingAccount.addTransaction(
                BankingTransaction
                        .create()
                        .setBankingAccount(anotherCustomerBankingAccount)
                        .setDescription("Netflix.us")
                        .setBalanceBefore(BigDecimal.valueOf(900))
                        .setBalanceAfter(BigDecimal.valueOf(870))
                        .setStatus(BankingTransactionStatus.PENDING)
                        .setAmount(BigDecimal.valueOf(30))
                        .setType(BankingTransactionType.TRANSFER_TO)
        );

        bankingAccountRepository.save(anotherCustomerBankingAccount);

        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/v1/banking/transactions/pending")
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andDo(print())
                                  .andExpect(status().is(200))
                                  .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        BankingTransactionDto[] transactionResponseDto = objectMapper.readValue(
                contentNode.toString(),
                BankingTransactionDto[].class
        );

        assertThat(transactionResponseDto).isNotNull();
        assertThat(transactionResponseDto)
                .allSatisfy(tx -> {
                    assertThat(tx.accountId()).isEqualTo(customerBankingAccount.getId());
                    assertThat(tx.status()).isEqualTo(BankingTransactionStatus.PENDING);
                });
    }

    @Test
    @DisplayName("Should get paged card transactions")
    void shouldGetCardTransactions() throws Exception {
        // given
        login(customer);
        BankingTransaction transaction = BankingTransaction
                .create()
                .setBankingAccount(customerBankingAccount)
                .setBankingCard(customerBankingCard)
                .setDescription("Amazon.com")
                .setBalanceBefore(BigDecimal.valueOf(100))
                .setBalanceAfter(BigDecimal.valueOf(0))
                .setStatus(BankingTransactionStatus.COMPLETED)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.CARD_CHARGE);

        customerBankingAccount.addTransaction(transaction);
        bankingAccountRepository.save(customerBankingAccount);

        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/v1/banking/cards/{id}/transactions", customerBankingCard.getId())
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andDo(print())
                                  .andExpect(status().is(200))
                                  .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        BankingTransactionDto[] transactionResponseDto = objectMapper.readValue(
                contentNode.toString(),
                BankingTransactionDto[].class
        );

        assertThat(transactionResponseDto).isNotNull();
        assertThat(transactionResponseDto)
                .allSatisfy(tx -> {
                    assertThat(tx.accountId()).isEqualTo(customerBankingAccount.getId());
                    assertThat(tx.cardId()).isEqualTo(customerBankingCard.getId());
                });

    }

    @Test
    @DisplayName("Should get paged account transactions")
    void shouldGetAccountTransactions() throws Exception {
        // given
        login(customer);

        // when
        // then
        mockMvc.perform(get("/api/v1/banking/accounts/{id}/transactions", customerBankingAccount.getId())
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(status().is(200));

    }
}