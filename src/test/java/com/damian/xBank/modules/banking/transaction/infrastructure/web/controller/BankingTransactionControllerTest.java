package com.damian.xBank.modules.banking.transaction.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
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
                .create(customer)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");

        customerBankingAccount.addTransaction(
                BankingTransaction.create(
                                          BankingTransactionType.CARD_CHARGE,
                                          customerBankingCard,
                                          BigDecimal.valueOf(100)
                                  )
                                  .setDescription("Amazon.com")
                                  .setStatus(BankingTransactionStatus.PENDING)
        );

        customerBankingAccount.addTransaction(
                BankingTransaction.create(
                                          BankingTransactionType.CARD_CHARGE,
                                          customerBankingCard,
                                          BigDecimal.valueOf(30)
                                  )
                                  .setDescription("Neyflix.com")
                                  .setStatus(BankingTransactionStatus.COMPLETED)
        );

        customerBankingAccount.addTransaction(
                BankingTransaction.create(
                                          BankingTransactionType.CARD_CHARGE,
                                          customerBankingCard,
                                          BigDecimal.valueOf(20)
                                  )
                                  .setDescription("HBO.com")
                                  .setStatus(BankingTransactionStatus.PENDING)
        );

        customerBankingAccount.addBankingCard(customerBankingCard);
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("GET /banking/transactions/{id} - should return the transaction for the logged user")
    void getTransaction_WhenValidId_ReturnsTransaction() throws Exception {
        // given
        login(customer);

        BankingTransaction transaction = BankingTransaction.create(
                                                                   BankingTransactionType.CARD_CHARGE,
                                                                   customerBankingCard,
                                                                   BigDecimal.valueOf(100)
                                                           )
                                                           .setDescription("Amazon.com")
                                                           .setStatus(BankingTransactionStatus.COMPLETED);

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
    @DisplayName("GET /banking/transactions/pending - should return only pending transactions for logged user")
    void getPendingTransactions_WhenLoggedUser_ReturnsOnlyPendingTransactions() throws Exception {
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
                .create(anotherCustomer)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setStatus(BankingAccountStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountNumber("US9900001111112233334444");

        anotherCustomerBankingAccount.addTransaction(
                BankingTransaction.create(
                                          BankingTransactionType.TRANSFER_TO,
                                          anotherCustomerBankingAccount,
                                          BigDecimal.valueOf(100)
                                  )
                                  .setDescription("Amazon.us")
                                  .setStatus(BankingTransactionStatus.PENDING)
        );

        anotherCustomerBankingAccount.addTransaction(
                BankingTransaction.create(
                                          BankingTransactionType.TRANSFER_TO,
                                          anotherCustomerBankingAccount,
                                          BigDecimal.valueOf(30)
                                  )
                                  .setDescription("Netflix.us")
                                  .setStatus(BankingTransactionStatus.PENDING)
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
    @DisplayName("GET /banking/cards/{id}/transactions - should return all transactions for the specified card")
    void getCardTransactions_WhenValidCardId_ReturnsPagedTransactions() throws Exception {
        // given
        login(customer);
        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        customerBankingCard,
                        BigDecimal.valueOf(100)
                )
                .setDescription("Amazon.us")
                .setStatus(BankingTransactionStatus.COMPLETED);

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
    @DisplayName("GET /banking/accounts/{id}/transactions - should return all transactions for the specified account")
    void getAccountTransactions_WhenValidAccountId_ReturnsPagedTransactions() throws Exception {
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