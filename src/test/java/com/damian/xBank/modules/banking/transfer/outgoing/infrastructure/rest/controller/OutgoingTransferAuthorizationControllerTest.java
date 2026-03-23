package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.FailedOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OutgoingTransferAuthorizationControllerTest extends AbstractControllerTest {

    private BankingAccount customerBankingAccount;
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
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
    void outgoingTransferFailure_Returns200OK() throws Exception {
        // given
        OutgoingTransfer transfer = OutgoingTransferTestBuilder.builder()
            .withAmount(BigDecimal.valueOf(100))
            .withFromAccount(customerBankingAccount)
            .withToAccount(null)
            .withToAccountIban("US12341234123412341234")
            .withDescription("description")
            .build();

        transfer.confirm();
        transfer.authorize("1234");
        bankingAccountRepository.save(customerBankingAccount);
        outgoingTransferRepository.save(transfer);

        OutgoingTransferFailureRequest request = new OutgoingTransferFailureRequest(
            transfer.getProviderAuthorizationId(),
            "Server error"
        );

        // when
        // then
        MvcResult result = mockMvc.perform(post("/api/v1/webhooks/transfers/outgoing/failed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonHelper.toJson(request)))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();

        FailedOutgoingTransferResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            FailedOutgoingTransferResult.class
        );

        // then
        assertThat(response)
            .isNotNull()
            .extracting(
                FailedOutgoingTransferResult::status,
                FailedOutgoingTransferResult::failureReason
            ).containsExactly(
                OutgoingTransferStatus.FAILED,
                request.failure()
            );
    }
}
