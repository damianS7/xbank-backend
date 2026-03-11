package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.confirm.ConfirmOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.confirm.ConfirmOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.BankingAccountTestBuilder;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
            .withId(1L)
            .withOwner(fromCustomer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US9900001111112233334444")
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
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return authorized transfer when request is valid")
    void confirmTransfer_WhenValidRequest_ReturnsAuthorizedTransfer() {
        // given
        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
            .create(fromAccount, toAccount, BigDecimal.valueOf(100))
            .setId(1L)
            .setStatus(BankingTransferStatus.PENDING)
            .setDescription("a gift!");

        ConfirmOutgoingTransferCommand command = new ConfirmOutgoingTransferCommand(
            givenTransfer.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingTransferRepository.findById(anyLong())).thenReturn(Optional.of(givenTransfer));

        // then
        confirmOutgoingTransfer.execute(command);

        assertThat(givenTransfer.getStatus()).isEqualTo(BankingTransferStatus.CONFIRMED);
        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }

    @Test
    @DisplayName("should throw exception when invalid password")
    void confirmTransfer_WhenInvalidPassword_ThrowsException() {
        // given
        setUpContext(fromCustomer);

        BankingTransfer givenTransfer = BankingTransfer
            .create(fromAccount, toAccount, BigDecimal.valueOf(100))
            .setId(1L)
            .setStatus(BankingTransferStatus.PENDING)
            .setDescription("a gift!");

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