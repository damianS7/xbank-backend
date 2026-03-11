package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccount;
import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccountCommand;
import com.damian.xBank.modules.banking.account.application.usecase.close.CloseAccountResult;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CloseAccountTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private CloseAccount closeAccount;

    private User customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
        ;

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("Should returns a closed a BankingAccount")
    void closeAccount_WhenValidRequest_ReturnsClosedBankingAccount() {
        // given
        setUpContext(customer);

        CloseAccountCommand command = new CloseAccountCommand(
            bankingAccount.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong()))
            .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        closeAccount.execute(command);

        // then
        Assertions.assertThat(bankingAccount.getStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository).findById(bankingAccount.getId());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should throws when BankingAccount when is suspended")
    void closeAccount_WhenAccountSuspended_ThrowsException() {
        // given
        setUpContext(customer);

        CloseAccountCommand command = new CloseAccountCommand(
            bankingAccount.getId(),
            RAW_PASSWORD
        );

        bankingAccount.suspend();

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
            BankingAccountSuspendedException.class,
            () -> closeAccount.execute(command)
        );

        // then
        assertThat(exception).hasMessage(ErrorCodes.BANKING_ACCOUNT_SUSPENDED);
    }

    @Test
    @DisplayName("Should throws when BankingAccount is not found")
    void closeAccount_WhenAccountNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        CloseAccountCommand command = new CloseAccountCommand(
            bankingAccount.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
            BankingAccountNotFoundException.class,
            () -> closeAccount.execute(command)
        );

        // then
        assertTrue(exception.getMessage().contains(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should throws exception when authenticated customer is not the owner of the account")
    void closeAccount_WhenAccountNotOwnedByCustomer_ThrowsException() {
        // given
        User customer2 = UserTestBuilder.aCustomer()
            .withId(2L)
            .withEmail("customer2@demo.com")
            .withPassword(RAW_PASSWORD)
            .build();

        setUpContext(customer2);

        CloseAccountCommand command = new CloseAccountCommand(
            bankingAccount.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountNotOwnerException exception = assertThrows(
            BankingAccountNotOwnerException.class,
            () -> closeAccount.execute(command)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER);
    }

    @Test
    @DisplayName("Should returns a closed BankingAccount when not owner but it is admin")
    void closeAccount_WhenAccountNotOwnedByCustomerButItIsAdmin_ThrowsException() {
        // given
        User customerAdmin = UserTestBuilder.aCustomer()
            .withId(2L)
            .withEmail("customerAdmin@demo.com")
            .withRole(UserRole.ADMIN)
            .withPassword(RAW_PASSWORD)
            .build();

        setUpContext(customerAdmin);

        CloseAccountCommand command = new CloseAccountCommand(
            bankingAccount.getId(),
            RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(bankingAccount);

        CloseAccountResult result = closeAccount.execute(command);

        // then
        assertThat(result.accountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

}