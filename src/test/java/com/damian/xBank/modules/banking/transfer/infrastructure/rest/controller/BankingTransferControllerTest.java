package com.damian.xBank.modules.banking.transfer.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.confirm.ConfirmOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.create.CreateOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.reject.RejectOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.ConfirmOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.CreateOutgoingTransferRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankingTransferControllerTest extends AbstractControllerTest {

    private User fromCustomer;
    private BankingAccount fromBankingAccount;
    private User toCustomer;
    private BankingAccount toBankingAccount;
    private User admin;
    private BankingTransfer transfer;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.aCustomer()
            .withEmail("fromCustomer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withStatus(UserStatus.VERIFIED)
            .build();

        userRepository.save(fromCustomer);

        fromBankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(3200))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        bankingAccountRepository.save(fromBankingAccount);

        toCustomer = UserTestBuilder.aCustomer()
            .withEmail("toCustomer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withStatus(UserStatus.VERIFIED)
            .build();

        userRepository.save(toCustomer);

        toBankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(200))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("DE1234567890123456789012")
            .build();

        bankingAccountRepository.save(toBankingAccount);

        admin = UserTestBuilder.aCustomer()
            .withEmail("admin@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withRole(UserRole.ADMIN)
            .build();

        userRepository.save(admin);

        transfer = BankingTransferTestBuilder.builder()
            .withFromAccount(fromBankingAccount)
            .withToAccount(toBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();
      
        transferRepository.save(transfer);
    }

    @Test
    @DisplayName("POST /transfers a valid request should create a transfer")
    void postTransfers_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(fromCustomer);

        CreateOutgoingTransferRequest request = new CreateOutgoingTransferRequest(
            fromBankingAccount.getId(),
            toBankingAccount.getAccountNumber(),
            "Enjoy!",
            BigDecimal.valueOf(100)
        );

        // when
        MvcResult result = mockMvc
            .perform(post(
                "/api/v1/banking/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(201))
            .andReturn();

        CreateOutgoingTransferResult transferDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CreateOutgoingTransferResult.class
        );

        // then
        assertThat(transferDto)
            .isNotNull()
            .extracting(
                CreateOutgoingTransferResult::id,
                CreateOutgoingTransferResult::amount,
                CreateOutgoingTransferResult::status,
                CreateOutgoingTransferResult::description
            ).containsExactly(
                transferDto.id(),
                request.amount(),
                BankingTransferStatus.PENDING,
                request.description()
            );
    }

    @Test
    @DisplayName("POST /transfers should return 409 Conflict when source and destination accounts are the same")
    void postTransfers_WhenSameAccount_Returns409Conflict() throws Exception {
        // given
        login(fromCustomer);

        CreateOutgoingTransferRequest request = new CreateOutgoingTransferRequest(
            fromBankingAccount.getId(),
            fromBankingAccount.getAccountNumber(),
            "Enjoy!",
            BigDecimal.valueOf(100)
        );

        // when
        mockMvc
            .perform(post(
                "/api/v1/banking/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(409))
            .andExpect(jsonPath("$.errorCode")
                .value(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT));

    }

    @Test
    @DisplayName("POST /transfers should return 409 Conflict when any account its closed")
    void postTransfers_WhenAccountClosed_Returns409Conflict() throws Exception {
        // given
        login(fromCustomer);

        toBankingAccount.close();
        bankingAccountRepository.save(toBankingAccount);

        CreateOutgoingTransferRequest request = new CreateOutgoingTransferRequest(
            fromBankingAccount.getId(),
            toBankingAccount.getAccountNumber(),
            "Enjoy!",
            BigDecimal.valueOf(100)
        );

        // when
        mockMvc
            .perform(post(
                "/api/v1/banking/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(409))
            .andExpect(jsonPath("$.errorCode")
                .value(ErrorCodes.BANKING_ACCOUNT_CLOSED));

    }

    @Test
    @DisplayName("POST /transfers should return 403 Unauthorized when any account its suspended")
    void postTransfers_WhenAccountSuspended_Returns403Unauthorized() throws Exception {
        // given
        login(fromCustomer);

        toBankingAccount.suspend();
        bankingAccountRepository.save(toBankingAccount);

        CreateOutgoingTransferRequest request = new CreateOutgoingTransferRequest(
            fromBankingAccount.getId(),
            toBankingAccount.getAccountNumber(),
            "Enjoy!",
            BigDecimal.valueOf(100)
        );

        // when
        mockMvc
            .perform(post(
                "/api/v1/banking/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(403))
            .andExpect(jsonPath("$.errorCode")
                .value(ErrorCodes.BANKING_ACCOUNT_SUSPENDED));

    }

    @Test
    @DisplayName("POST /transfers should return 409 Conflict when source account has no funds")
    void postTransfers_WhenInsufficientFunds_Returns409Conflict() throws Exception {
        // given
        login(fromCustomer);

        CreateOutgoingTransferRequest request = new CreateOutgoingTransferRequest(
            fromBankingAccount.getId(),
            toBankingAccount.getAccountNumber(),
            "Enjoy!",
            BigDecimal.valueOf(999999999)
        );

        // when
        mockMvc
            .perform(post(
                "/api/v1/banking/transfers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(409))
            .andExpect(jsonPath("$.errorCode")
                .value(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS));
    }

    // TODO review naming of this test.
    @Test
    @DisplayName("POST /transfers/confirm a valid request should confirm a transfer")
    void postTransfersConfirm_ValidRequest_Returns200OK() throws Exception {
        // given
        login(fromCustomer);

        ConfirmOutgoingTransferRequest request = new ConfirmOutgoingTransferRequest(
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(post(
                "/api/v1/banking/transfers/{id}/confirm", transfer.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        ConfirmOutgoingTransferResult transferDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            ConfirmOutgoingTransferResult.class
        );

        // then
        assertThat(transferDto)
            .isNotNull()
            .extracting(
                ConfirmOutgoingTransferResult::id,
                ConfirmOutgoingTransferResult::amount,
                ConfirmOutgoingTransferResult::status,
                ConfirmOutgoingTransferResult::description
            ).containsExactly(
                transfer.getId(),
                transfer.getAmount().setScale(2),
                BankingTransferStatus.CONFIRMED,
                transfer.getDescription()
            );
    }

    @Test
    @DisplayName("POST /transfers/confirm with invalid password should returns 403 Unauthorized")
    void postTransfersConfirm_InvalidPassword_Returns403Unauthorized() throws Exception {
        // given
        login(fromCustomer);

        ConfirmOutgoingTransferRequest request = new ConfirmOutgoingTransferRequest(
            "BAD_PASSWORD"
        );

        // when
        mockMvc
            .perform(post(
                "/api/v1/banking/transfers/{id}/confirm", transfer.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(403))
            .andExpect(jsonPath("$.errorCode")
                .value(ErrorCodes.USER_INVALID_PASSWORD));

    }

    @Test
    @DisplayName("POST /transfers/reject a valid request should reject a transfer")
    void postTransfersReject_ValidRequest_Returns200OK() throws Exception {
        // given
        login(fromCustomer);

        ConfirmOutgoingTransferRequest request = new ConfirmOutgoingTransferRequest(
            RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
            .perform(post(
                "/api/v1/banking/transfers/{id}/reject", transfer.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        RejectOutgoingTransferResult transferDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            RejectOutgoingTransferResult.class
        );

        // then
        assertThat(transferDto)
            .isNotNull()
            .extracting(
                RejectOutgoingTransferResult::id,
                RejectOutgoingTransferResult::amount,
                RejectOutgoingTransferResult::status,
                RejectOutgoingTransferResult::description
            ).containsExactly(
                transfer.getId(),
                transfer.getAmount().setScale(2),
                BankingTransferStatus.REJECTED,
                transfer.getDescription()
            );

    }
}