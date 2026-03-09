package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.AuthorizeOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizeOutgoingInternalTransferTest extends AbstractServiceTest {

    @InjectMocks
    private AuthorizeOutgoingInternalTransfer authorizeOutgoingInternalTransfer;

    @Mock
    private NotificationEventFactory notificationEventFactory;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

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
        final BigDecimal fromAccountInitialBalance = fromAccount.getBalance();
        final BigDecimal toAccountInitialBalance = toAccount.getBalance();

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

        // then
        authorizeOutgoingInternalTransfer.execute(command);

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));

        // check balances
        assertThat(fromAccount.getBalance())
            .isEqualTo(fromAccountInitialBalance.subtract(givenTransfer.getAmount()));

        assertThat(toAccount.getBalance())
            .isEqualTo(toAccountInitialBalance.add(givenTransfer.getAmount()));
    }
}