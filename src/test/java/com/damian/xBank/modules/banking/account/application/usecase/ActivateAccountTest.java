package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccount;
import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccountResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ActivateAccountTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private ActivateAccount activateAccount;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("should return active account when admin tries to activate suspended account")
    void execute_WhenSuspendedAccountActiveByAdmin_ReturnActiveAccount() {
        // given
        User admin = UserTestFactory.anAdmin()
            .withId(1L)
            .build();

        setUpContext(admin);

        bankingAccount.suspend();

        ActivateAccountCommand command = new ActivateAccountCommand(
            bankingAccount.getId()
        );

        // when
        when(bankingAccountRepository.findById(anyLong()))
            .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
            .thenAnswer(i -> i.getArgument(0));

        ActivateAccountResult result = activateAccount.execute(command);

        // then
        assertThat(result)
            .isNotNull()
            .extracting(ActivateAccountResult::accountStatus)
            .isEqualTo(BankingAccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throws exception when trying to activate closed account")
    void execute_WhenClosedAccount_ThrowsException() {
        // given
        User admin = UserTestFactory.anAdmin()
            .withId(1L)
            .build();

        setUpContext(admin);

        bankingAccount.close();

        ActivateAccountCommand command = new ActivateAccountCommand(
            bankingAccount.getId()
        );

        // when
        when(bankingAccountRepository.findById(anyLong()))
            .thenReturn(Optional.of(bankingAccount));

        BankingAccountStatusTransitionException exception = assertThrows(
            BankingAccountStatusTransitionException.class,
            () -> activateAccount.execute(command)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, exception.getMessage());
    }
}