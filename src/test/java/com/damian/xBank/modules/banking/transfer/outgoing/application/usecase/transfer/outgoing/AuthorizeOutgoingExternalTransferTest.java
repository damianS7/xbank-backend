package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.outgoing.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingExternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizeOutgoingExternalTransferTest extends AbstractServiceTest {

    @InjectMocks
    private AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer;

    @Mock
    private NotificationEventFactory notificationEventFactory;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

    @Mock
    private TransferAuthorizationGateway transferAuthorizationGateway;

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("fromCustomer@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        fromAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233334444")
            .build();

        toCustomer = UserTestBuilder.builder()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid")
    void authorizeTransfer_WhenValidRequest_ReturnsAuthorizedTransfer() {
        // given
        //        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = OutgoingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.confirm();

        //        BankingTransaction fromTransaction = BankingTransaction
        //            .create(
        //                BankingTransactionType.TRANSFER_TO,
        //                fromAccount,
        //                givenTransfer.getAmount()
        //            )
        //            .setStatus(BankingTransactionStatus.PENDING)
        //            .setDescription(givenTransfer.getDescription());
        //
        //        BankingTransaction toTransaction = BankingTransaction
        //            .create(
        //                BankingTransactionType.TRANSFER_FROM,
        //                toAccount,
        //                givenTransfer.getAmount()
        //            )
        //            .setStatus(BankingTransactionStatus.PENDING)
        //            .setDescription(givenTransfer.getDescription());
        //
        //        givenTransfer.addTransaction(fromTransaction);
        //        givenTransfer.addTransaction(toTransaction);

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(outgoingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));
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
        //        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = OutgoingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(null)
            .withToAccountIban("US1200001111112233335555")
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.confirm();

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(outgoingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));
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