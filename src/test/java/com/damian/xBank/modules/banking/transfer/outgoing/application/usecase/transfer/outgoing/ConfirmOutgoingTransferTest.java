package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm.ConfirmOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm.ConfirmOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestBuilder;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.OutgoingTransferTestBuilder;
import com.damian.xBank.test.utils.OutgoingTransferTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfirmOutgoingTransferTest extends AbstractServiceTest {

    @InjectMocks
    private ConfirmOutgoingTransfer confirmOutgoingTransfer;

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

        fromAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US9900001111112233334444")
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
    @DisplayName("should return authorized transfer when request is valid")
    void confirmTransfer_WhenValidRequest_ReturnsAuthorizedTransfer() {
        // given
        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = OutgoingTransferTestFactory.anInternalTransfer(fromAccount, toAccount)
            .withId(1L)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        ConfirmOutgoingTransferCommand command = new ConfirmOutgoingTransferCommand(
            givenTransfer.getId(),
            RAW_PASSWORD
        );

        // when
        when(outgoingTransferRepository.findById(anyLong()))
            .thenReturn(Optional.of(givenTransfer));

        // then
        confirmOutgoingTransfer.execute(command);

        assertThat(givenTransfer.getStatus()).isEqualTo(OutgoingTransferStatus.CONFIRMED);
        verify(outgoingTransferRepository, times(1)).save(any(OutgoingTransfer.class));
    }

    @Test
    @DisplayName("should throw exception when invalid password")
    void confirmTransfer_WhenInvalidPassword_ThrowsException() {
        // given
        setUpContext(fromCustomer);

        OutgoingTransfer givenTransfer = OutgoingTransferTestBuilder.builder()
            .withId(1L)
            .withFromAccount(fromAccount)
            .withToAccount(toAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withDescription("a gift!")
            .build();

        ConfirmOutgoingTransferCommand command = new ConfirmOutgoingTransferCommand(
            givenTransfer.getId(),
            "WRONG PASSWORD"
        );

        // when
        // then
        UserInvalidPasswordConfirmationException exception = assertThrows(
            UserInvalidPasswordConfirmationException.class,
            () -> confirmOutgoingTransfer.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.USER_INVALID_PASSWORD);
    }
}