package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.complete.CompleteOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
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
        fromCustomer = UserTestFactory.aCustomer()
            .withId(1L)
            .withEmail("fromCustomer@demo.com")
            .build();

        fromAccount = BankingAccountTestFactory.aSavingsAccount(fromCustomer)
            .withId(5L)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withAccountNumber("ES1234567890123456789012")
            .build();

        toCustomer = UserTestFactory.aCustomer()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .build();

        toAccount = BankingAccountTestFactory.aSavingsAccount(toCustomer)
            .withId(2L)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withAccountNumber("ES1234567890123456789012")
            .build();
    }

    @Test
    @DisplayName("should return completed transfer when request is valid")
    void completeTransfer_ReturnsConfirmedTransfer() {
        // given
        final BigDecimal fromAccountInitialBalance = fromAccount.getBalance();
        final BigDecimal toAccountInitialBalance = toAccount.getBalance();

        OutgoingTransfer givenTransfer = OutgoingTransferTestFactory.anInternalTransfer(fromAccount, toAccount)
            .withId(1L)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.confirm();
        givenTransfer.authorize("1234");

        // when
        //        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        // then
        completeOutgoingInternalTransfer.execute(givenTransfer);

        verify(outgoingTransferRepository, times(1))
            .save(any(OutgoingTransfer.class));

        // check balances
        assertThat(givenTransfer.getStatus()).isEqualTo(OutgoingTransferStatus.COMPLETED);
        assertThat(fromAccount.getBalance())
            .isEqualTo(fromAccountInitialBalance.subtract(givenTransfer.getAmount()));

        assertThat(toAccount.getBalance())
            .isEqualTo(toAccountInitialBalance.add(givenTransfer.getAmount()));
    }
}