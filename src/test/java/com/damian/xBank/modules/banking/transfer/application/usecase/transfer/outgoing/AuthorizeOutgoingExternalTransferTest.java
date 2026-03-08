package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize.AuthorizeOutgoingExternalTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.response.TransferAuthorizationResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
    private BankingTransferRepository bankingTransferRepository;

    @Mock
    private TransferAuthorizationGateway transferAuthorizationGateway;

    private User fromCustomer;
    private User toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("fromCustomer@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        fromAccount = BankingAccount
            .create(fromCustomer)
            .setId(1L)
            .setBalance(BigDecimal.valueOf(1000))
            .setCurrency(BankingAccountCurrency.EUR)
            .setType(BankingAccountType.SAVINGS)
            .setAccountNumber("US9900001111112233334444");

        toCustomer = UserTestBuilder.aCustomer()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        toAccount = BankingAccount
            .create(toCustomer)
            .setId(2L)
            .setBalance(BigDecimal.valueOf(1000))
            .setCurrency(BankingAccountCurrency.EUR)
            .setType(BankingAccountType.SAVINGS)
            .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid")
    void authorizeTransfer_WhenValidRequest_ReturnsAuthorizedTransfer() {
        // given
        //        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
            .create(fromAccount, toAccount, BigDecimal.valueOf(100))
            .setId(1L)
            .setStatus(BankingTransferStatus.CONFIRMED)
            .setDescription("a gift!");

        BankingTransaction fromTransaction = BankingTransaction
            .create(
                BankingTransactionType.TRANSFER_TO,
                fromAccount,
                givenTransfer.getAmount()
            )
            .setStatus(BankingTransactionStatus.PENDING)
            .setDescription(givenTransfer.getDescription());

        BankingTransaction toTransaction = BankingTransaction
            .create(
                BankingTransactionType.TRANSFER_FROM,
                toAccount,
                givenTransfer.getAmount()
            )
            .setStatus(BankingTransactionStatus.PENDING)
            .setDescription(givenTransfer.getDescription());

        givenTransfer.addTransaction(fromTransaction);
        givenTransfer.addTransaction(toTransaction);

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));
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

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid for external transfer")
    void authorizeTransfer_WhenExternalTransfer_ReturnsAuthorizedTransfer() {
        // given
        //        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
            .create(fromAccount, null, BigDecimal.valueOf(100))
            .setId(1L)
            .setStatus(BankingTransferStatus.CONFIRMED)
            .setType(BankingTransferType.EXTERNAL)
            .setToAccountIban("US1200001111112233335555")
            .setDescription("a gift!");

        AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(
            givenTransfer.getId()
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));
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
                BankingTransfer::getStatus,
                BankingTransfer::getProviderAuthorizationId
            ).containsExactly(
                BankingTransferStatus.AUTHORIZED,
                "123/123"
            );

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }
}