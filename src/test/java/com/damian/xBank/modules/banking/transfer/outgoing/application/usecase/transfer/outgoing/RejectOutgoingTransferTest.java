package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class RejectOutgoingTransferTest extends AbstractServiceTest {

    @InjectMocks
    private RejectOutgoingTransfer rejectOutgoingTransfer;

    @Mock
    private OutgoingTransferRepository outgoingTransferRepository;

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
            .withAccountNumber("US1200001111112233334444")
            .build();

        toCustomer = UserTestFactory.aCustomer()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .build();

        toAccount = BankingAccountTestFactory.aSavingsAccount(toCustomer)
            .withId(2L)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return rejected transfer")
    void rejectTransfer_WhenValidRequest_ReturnsRejectedTransfer() {
        // given
        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = OutgoingTransferTestFactory.anInternalTransfer(fromAccount, toAccount)
            .withId(1L)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        givenTransfer.reject("rejected");

        RejectOutgoingTransferCommand command = new RejectOutgoingTransferCommand(
            givenTransfer.getId(),
            RAW_PASSWORD
        );

        // when
        when(outgoingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        // then
        rejectOutgoingTransfer.execute(command);
    }
}