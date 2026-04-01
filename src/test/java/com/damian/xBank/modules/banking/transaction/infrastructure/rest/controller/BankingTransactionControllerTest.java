package com.damian.xBank.modules.banking.transaction.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingTransactionControllerTest extends AbstractControllerTest {
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

        customerBankingCard = customerBankingAccount.issueCard(
            BankingCardType.CREDIT,
            "1234123412341234",
            "123",
            "1234"
        );
        customerBankingCard.activate(customerBankingCard.getCardCvv());

        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("GET /banking/transactions/{id} - should return the transaction for the logged user")
    void getTransaction_WhenValidId_ReturnsTransaction() throws Exception {
        // given
        login(customer);

        BankingTransaction transaction = BankingTransaction.createCardTransaction(
            BankingTransactionType.CARD_CHARGE,
            customerBankingCard,
            BigDecimal.valueOf(120),
            "Amazon.com"
        );

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
    void getPendingTransactions_ReturnsOnlyPendingTransactions() throws Exception {
        // given
        login(customer);

        BankingTransaction transaction = BankingTransactionTestFactory.aCardChargeTransaction()
            .withCard(customerBankingCard)
            .withAmount(BigDecimal.valueOf(120))
            .withStatus(BankingTransactionStatus.PENDING)
            .withPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withDescription("Amazon.com")
            .build();

        BankingTransaction transaction2 = BankingTransactionTestFactory.aCardChargeTransaction()
            .withCard(customerBankingCard)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withAmount(BigDecimal.valueOf(120))
            .withDescription("Amazon.com")
            .withStatus(BankingTransactionStatus.COMPLETED)
            .build();

        transactionRepository.saveAll(Set.of(transaction, transaction2));

        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/v1/banking/transactions/pending")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        PageResult<BankingTransactionResult> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        assertThat(response).isNotNull();
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content())
            .allSatisfy(tx -> {
                assertThat(tx.bankingAccountId()).isEqualTo(customerBankingAccount.getId());
                assertThat(tx.status()).isEqualTo(BankingTransactionStatus.PENDING);
            });
    }

    @Test
    @DisplayName("GET /banking/cards/{id}/transactions - should return all transactions for the specified card")
    void getCardTransactions_WhenValidCardId_ReturnsPagedTransactions() throws Exception {
        // given
        login(customer);
        BankingTransaction transaction = BankingTransactionTestFactory.aCardChargeTransaction()
            .withCard(customerBankingCard)
            .withAmount(BigDecimal.valueOf(100))
            .withStatus(BankingTransactionStatus.COMPLETED)
            .withPaymentStatus(BankingTransactionPaymentStatus.AUTHORIZED)
            .withType(BankingTransactionType.CARD_CHARGE)
            .withDescription("Amazon.com")
            .build();

        transactionRepository.save(transaction);

        // when
        // then
        MvcResult result = mockMvc.perform(get("/api/v1/banking/cards/{id}/transactions", customerBankingCard.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        PageResult<BankingTransactionResult> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        assertThat(response).isNotNull();
        assertThat(response.content())
            .allSatisfy(tx -> {
                assertThat(tx.bankingAccountId()).isEqualTo(customerBankingAccount.getId());
                assertThat(tx.bankingCardId()).isEqualTo(customerBankingCard.getId());
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