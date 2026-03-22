package com.damian.xBank.modules.banking.transfer.incoming.infrastructure;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete.CompleteIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransferStatus;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.CompleteIncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IncomingTransferControllerTest extends AbstractControllerTest {

    private User fromCustomer;
    private BankingAccount fromBankingAccount;
    private User toCustomer;
    private BankingAccount toBankingAccount;
    private User admin;
    private OutgoingTransfer transfer;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.builder()
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

        toCustomer = UserTestBuilder.builder()
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

        admin = UserTestBuilder.builder()
            .withEmail("admin@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withRole(UserRole.ADMIN)
            .build();

        userRepository.save(admin);

        transfer = OutgoingTransferTestBuilder.builder()
            .withFromAccount(fromBankingAccount)
            .withToAccount(toBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        outgoingTransferRepository.save(transfer);
    }

    @Test
    @DisplayName("Authorize incoming transfer")
    void authorizeIncomingTransfer_AuthorizesTransfer() throws Exception {
        // given
        login(fromCustomer);

        AuthorizeIncomingTransferRequest request = new AuthorizeIncomingTransferRequest(
            "1234/1234",
            "ES0011223344556677",
            toBankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "DAVID"
        );

        // when
        MvcResult result = mockMvc
            .perform(post(
                "/api/v1/webhooks/transfers/incoming/authorize")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        AuthorizeIncomingTransferResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthorizeIncomingTransferResult.class
        );

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::authorizationId,
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                request.authorizationId(),
                TransferAuthorizationStatus.AUTHORIZED,
                null
            );
    }

    @Test
    void authorizeIncomingTransfer_WhenAccountNotFound_ReturnsRejectedResponseAnd200OK() throws Exception {
        // given
        AuthorizeIncomingTransferRequest request = new AuthorizeIncomingTransferRequest(
            "1234",
            "ES001188222832838",
            toBankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        // when
        when(bankingAccountRepository.findByAccountNumber(anyString()))
            .thenReturn(Optional.empty());

        // then
        MvcResult result = mockMvc.perform(post("/api/v1/webhooks/transfers/incoming/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonHelper.toJson(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        AuthorizeIncomingTransferResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthorizeIncomingTransferResult.class
        );

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                AuthorizeIncomingTransferResult::status,
                AuthorizeIncomingTransferResult::rejectionReason
            ).containsExactly(
                TransferAuthorizationStatus.REJECTED,
                ErrorCodes.BANKING_ACCOUNT_NOT_FOUND
            );
    }

    @Test
    @DisplayName("Completes incoming transfer and increases account balance")
    void completeIncomingTransfer_WhenAuthorized_ThenCompleteTransferAndIncreaseBalance() throws Exception {
        // given
        login(fromCustomer);

        BigDecimal accountInitialBalance = fromBankingAccount.getBalance();

        IncomingTransfer incomingTransfer = IncomingTransfer.create(
            "1234 1234 1234 1234 1234",
            fromBankingAccount,
            fromBankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "David"
        );
        incomingTransfer.authorize("1234/1234");
        incomingTransferRepository.save(incomingTransfer);

        CompleteIncomingTransferRequest request = new CompleteIncomingTransferRequest(
            incomingTransfer.getProviderAuthorizationId()
        );

        // when
        MvcResult result = mockMvc
            .perform(post(
                "/api/v1/webhooks/transfers/incoming/complete")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        CompleteIncomingTransferResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CompleteIncomingTransferResult.class
        );

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                CompleteIncomingTransferResult::status
            )
            .isEqualTo(
                IncomingTransferStatus.COMPLETED
            );

        BankingAccount updatedAccount = bankingAccountRepository
            .findById(fromBankingAccount.getId())
            .orElseThrow();

        assertThat(updatedAccount.getBalance())
            .isEqualTo(accountInitialBalance.add(incomingTransfer.getAmount()).setScale(2));
    }

}