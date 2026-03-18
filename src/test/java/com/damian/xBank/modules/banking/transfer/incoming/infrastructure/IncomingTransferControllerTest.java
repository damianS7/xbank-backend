package com.damian.xBank.modules.banking.transfer.incoming.infrastructure;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.BankingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
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

        transfer = BankingTransferTestBuilder.builder()
            .withFromAccount(fromBankingAccount)
            .withToAccount(toBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        outgoingTransferRepository.save(transfer);
    }

    @Test
    @DisplayName("")
    void authorizeIncomingTransfer_WhenValidRequest_Returns200Ok() throws Exception {
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

        AuthorizeIncomingTransferResult resultDto = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AuthorizeIncomingTransferResult.class
        );

        // then
        assertThat(resultDto)
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

}