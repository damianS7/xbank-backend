package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.complete.CompleteOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferTestBuilder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ConfirmOutgoingInternalTransferTest extends AbstractServiceTest {

    @InjectMocks
    private CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer;

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

        fromAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        toCustomer = UserTestBuilder.aCustomer()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(2L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();
    }

    @Test
    @DisplayName("should return completed transfer when request is valid")
    void completeTransfer_ReturnsConfirmedTransfer() {
        // given
        //        setUpContext(fromCustomer);
        final BigDecimal fromAccountInitialBalance = fromAccount.getBalance();
        final BigDecimal toAccountInitialBalance = toAccount.getBalance();

        BankingTransfer givenTransfer = BankingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.confirm();
        givenTransfer.authorize("1234");
        //
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

        // when
        //        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        // then
        completeOutgoingInternalTransfer.execute(givenTransfer);

        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));

        // check balances
        assertThat(givenTransfer.getStatus()).isEqualTo(BankingTransferStatus.COMPLETED);
        assertThat(fromAccount.getBalance())
            .isEqualTo(fromAccountInitialBalance.subtract(givenTransfer.getAmount()));

        assertThat(toAccount.getBalance())
            .isEqualTo(toAccountInitialBalance.add(givenTransfer.getAmount()));
    }
}