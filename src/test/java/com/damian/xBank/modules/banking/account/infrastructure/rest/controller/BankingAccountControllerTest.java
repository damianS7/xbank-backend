package com.damian.xBank.modules.banking.account.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.application.dto.BankingAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.create.CreateAccountResult;
import com.damian.xBank.modules.banking.account.application.usecase.request.RequestCardResult;
import com.damian.xBank.modules.banking.account.application.usecase.set.alias.SetAccountAliasResult;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.CloseBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.CreateBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.DepositBankingAccountRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.RequestBankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.infrastructure.rest.request.SetBankingAccountAliasRequest;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.BankingAccountTestBuilder;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingAccountControllerTest extends AbstractControllerTest {
    private User admin;
    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer().build();
        userRepository.save(customer);

        admin = UserTestFactory.anAdmin()
            .withEmail("admin@demo.com")
            .build();
        userRepository.save(admin);

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
        bankingAccountRepository.save(bankingAccount);
    }

    @Test
    @DisplayName("should return all the customer banking accounts")
    void getAccounts_WhenValidRequest_ThenReturnBankingAccount() throws Exception {
        // given
        login(customer);

        // when
        MvcResult result = mockMvc
            .perform(get("/api/v1/banking/accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        BankingAccountResult[] bankingAccountDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            BankingAccountResult[].class
        );

        // then
        assertThat(bankingAccountDto[0])
            .isNotNull()
            .extracting(
                BankingAccountResult::id,
                BankingAccountResult::accountNumber,
                BankingAccountResult::accountStatus
            ).containsExactly(
                bankingAccount.getId(),
                bankingAccount.getAccountNumber(),
                bankingAccount.getStatus()
            );
    }

    @Test
    @DisplayName("should create banking account with valid request")
    void postAccounts_WhenValidRequest_ThenReturnCreatedAccount() throws Exception {
        // given
        login(customer);
        CreateBankingAccountRequest request = new CreateBankingAccountRequest(
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

        CreateAccountResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CreateAccountResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isNotEmpty();
        assertThat(response.accountCurrency()).isEqualTo(request.currency());
        assertThat(response.balance()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.accountType()).isEqualTo(request.type());
    }

    @Test
    @DisplayName("should return 400 when request is invalid")
    void postAccounts_WhenAllFieldsMissing_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        CreateBankingAccountRequest request = new CreateBankingAccountRequest(
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

    @Test
    @DisplayName("should return 400 Bad Request when account type is missing")
    void postAccounts_WhenMissingAccountType_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        CreateBankingAccountRequest request = new CreateBankingAccountRequest(
            null,
            BankingAccountCurrency.EUR
        );

        // when & then
        mockMvc
            .perform(post("/api/v1/banking/accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 Bad Request when currency is missing")
    void postAccounts_WhenMissingCurrency_ThenReturn400BadRequest() throws Exception {
        // given
        login(customer);
        CreateBankingAccountRequest request = new CreateBankingAccountRequest(
            BankingAccountType.SAVINGS,
            null
        );

        // when & then
        mockMvc
            .perform(post("/api/v1/banking/accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 401 Unauthorized when user is not authenticated")
    void postAccounts_WhenNotAuthenticated_ThenReturn401Unauthorized() throws Exception {
        // given
        CreateBankingAccountRequest request = new CreateBankingAccountRequest(
            BankingAccountType.SAVINGS,
            BankingAccountCurrency.EUR
        );

        // when & then
        mockMvc
            .perform(post("/api/v1/banking/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return banking account closed")
    void postAccounts_WhenValidRequest_ThenReturnBankingAccountClosed() throws Exception {
        // given
        login(admin);

        CloseBankingAccountRequest request = new CloseBankingAccountRequest(
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(patch("/api/v1/banking/accounts/{id}/close", bankingAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        CloseAccountResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CloseAccountResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.accountStatus())
            .isEqualTo(BankingAccountStatus.CLOSED);
    }

    @Test
    @DisplayName("should return banking account with new alias")
    void postAccounts_WhenValidRequest_ThenReturnBankingAccountAliasUpdated() throws Exception {
        // given
        login(customer);

        SetBankingAccountAliasRequest request = new SetBankingAccountAliasRequest(
            "account for savings"
        );

        // when
        MvcResult result = mockMvc
            .perform(patch("/api/v1/banking/accounts/{id}/alias", bankingAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        SetAccountAliasResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            SetAccountAliasResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.alias()).isEqualTo(request.alias());
    }

    @Test
    @DisplayName("should return a new BankingCard")
    void postAccounts_WhenValidRequest_ThenReturnBankingCard() throws Exception {
        // given
        login(customer);

        RequestBankingAccountCardRequest request = new RequestBankingAccountCardRequest(
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

        RequestCardResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            RequestCardResult.class
        );

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                RequestCardResult::bankingAccountId,
                RequestCardResult::cardType
            )
            .containsExactly(
                bankingAccount.getId(),
                request.type()
            );
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