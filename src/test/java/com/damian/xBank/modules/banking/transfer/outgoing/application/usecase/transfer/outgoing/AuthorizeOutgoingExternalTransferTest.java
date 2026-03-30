package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transfer.outgoing.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingExternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.OutgoingTransferTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizeOutgoingExternalTransferTest extends AbstractServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

    @Mock
    private TransferAuthorizationGateway transferAuthorizationGateway;

    @InjectMocks
    private AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer;

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestFactory.aCustomer()
            .withId(1L)
            .withEmail("fromCustomer@demo.com")
            .build();

        fromAccount = BankingAccountTestFactory.aSavingsAccount(fromCustomer)
            .withId(1L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        toCustomer = UserTestFactory.aCustomer()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .build();

        toAccount = BankingAccountTestFactory.aSavingsAccount(toCustomer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid")
    void authorizeTransfer_WhenValidRequest_ReturnsAuthorizedTransfer() {
        // given
        OutgoingTransfer givenTransfer = OutgoingTransferTestFactory.anInternalTransfer(fromAccount, toAccount)
            .withId(1L)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();
        givenTransfer.confirm();

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(outgoingTransferRepository.findById(anyLong()))
            .thenReturn(Optional.of(givenTransfer));
        when(transferAuthorizationGateway.authorizeTransfer(any(TransferAuthorizationRequest.class)))
            .thenReturn(
                new TransferAuthorizationResponse(
                    "123/123",
                    TransferAuthorizationStatus.PENDING,
                    null
                )
            );

        // then
        authorizeOutgoingExternalTransfer.execute(command);

        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid for external transfer")
    void authorizeTransfer_WhenExternalTransfer_ReturnsAuthorizedTransfer() {
        // given
        OutgoingTransfer givenTransfer = OutgoingTransferTestFactory.aExternalTransfer(
                fromAccount,
                "US1200001111112233335555"
            )
            .withId(1L)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.confirm();

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(outgoingTransferRepository.findById(anyLong()))
            .thenReturn(Optional.of(givenTransfer));
        when(transferAuthorizationGateway.authorizeTransfer(
            any()
        )).thenReturn(
            new TransferAuthorizationResponse(
                "123/123",
                TransferAuthorizationStatus.PENDING,
                null
            )
        );

        // then
        authorizeOutgoingExternalTransfer.execute(command);

        assertThat(givenTransfer)
            .extracting(
                OutgoingTransfer::getStatus,
                OutgoingTransfer::getProviderAuthorizationId
            ).containsExactly(
                OutgoingTransferStatus.AUTHORIZED,
                "123/123"
            );

        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));
    }
}