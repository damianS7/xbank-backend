package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.complete.CompleteOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ConfirmOutgoingInternalTransferTest extends AbstractServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

    @InjectMocks
    private CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer;

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
            .withId(5L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("ES1234567890123456789012")
            .build();

        toCustomer = UserTestBuilder.builder()
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

        OutgoingTransfer givenTransfer = OutgoingTransferTestBuilder.builder()
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

        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));

        // check balances
        assertThat(givenTransfer.getStatus()).isEqualTo(OutgoingTransferStatus.COMPLETED);
        assertThat(fromAccount.getBalance())
            .isEqualTo(fromAccountInitialBalance.subtract(givenTransfer.getAmount()));

        assertThat(toAccount.getBalance())
            .isEqualTo(toAccountInitialBalance.add(givenTransfer.getAmount()));
    }
}