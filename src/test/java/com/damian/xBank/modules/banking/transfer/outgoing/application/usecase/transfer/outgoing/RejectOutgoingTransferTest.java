package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.BankingTransferTestBuilder;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
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
            .withAccountNumber("US1200001111112233334444")
            .build();

        toCustomer = UserTestBuilder.builder()
            .withId(2L)
            .withEmail("toCustomer@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        toAccount = BankingAccountTestBuilder.builder()
            .withId(2L)
            .withOwner(toCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return rejected transfer")
    void rejectTransfer_WhenValidRequest_ReturnsRejectedTransfer() {
        // given
        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = BankingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
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