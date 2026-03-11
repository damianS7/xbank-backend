package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccount;
import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.activate.ActivateAccountResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
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
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(1L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("should return active account when admin tries to activate suspended account")
    void execute_WhenSuspendedAccountActiveByAdmin_ReturnActiveAccount() {
        // given
        customer.setRole(UserRole.ADMIN);
        setUpContext(customer);

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
    @DisplayName("Should throws exception when customer tries to activate suspended account")
    void execute_WhenSuspendedAccountActiveByCustomer_ReturnSuspendedAccount() {
        // given
        setUpContext(customer);

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
            .isEqualTo(BankingAccountStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Should throws exception when trying to activate closed account")
    void execute_WhenClosedAccount_ThrowsException() {
        // given
        customer.setRole(UserRole.ADMIN);
        setUpContext(customer);

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