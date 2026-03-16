package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransferAuthorizationGatewayControllerTest extends AbstractControllerTest {

    private BankingAccount customerBankingAccount;
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withEmail("customer@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withPassword(passwordEncoder.encode(RAW_PASSWORD))
            .build();

        userRepository.save(customer);

        customerBankingAccount = BankingAccountTestBuilder.builder()
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    @DisplayName("")
    void authorizeIncomingTransfer_WhenValidRequest_Returns200OK() throws Exception {
        // given
        AuthorizeIncomingTransferRequest request = new AuthorizeIncomingTransferRequest(
            "1234",
            "ES001188222832838",
            customerBankingAccount.getAccountNumber(),
            BigDecimal.valueOf(100),
            "EUR",
            "from David"
        );

        // when
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
                AuthorizeIncomingTransferResult::status
            ).isEqualTo(
                TransferAuthorizationStatus.AUTHORIZED
            );
    }

    // TODO check BIN number?
    // TODO check account not found ... currency ... disabled
    // TODO notify failure
    // TODO incoming transfer
}
