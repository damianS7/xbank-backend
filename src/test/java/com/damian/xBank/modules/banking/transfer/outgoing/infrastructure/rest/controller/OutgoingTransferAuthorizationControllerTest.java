package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.FailedOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.OutgoingTransferTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
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
        customer = UserTestFactory.aCustomer()
            .build();
        userRepository.save(customer);

        customerBankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
        bankingAccountRepository.save(customerBankingAccount);
    }

    @Test
    void outgoingTransferFailure_Returns200OK() throws Exception {
        // given
        OutgoingTransfer transfer = OutgoingTransferTestFactory.aExternalTransfer(
                customerBankingAccount,
                "US12341234123412341234"
            )
            .withAmount(BigDecimal.valueOf(100))
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
