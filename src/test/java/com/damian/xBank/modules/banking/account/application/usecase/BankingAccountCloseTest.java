package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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
import static org.mockito.Mockito.*;

public class BankingAccountCloseTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @InjectMocks
    private BankingAccountClose bankingAccountClose;

    private Customer customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        bankingAccount = BankingAccount
                .create(customer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("Should returns a closed a BankingAccount")
    void closeAccount_WhenValidRequest_ReturnsClosedBankingAccount() {
        // given
        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingAccountClose.execute(
                bankingAccount.getId(),
                request
        );

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

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        bankingAccount.setStatus(BankingAccountStatus.ACTIVE);
        bankingAccount.setStatus(BankingAccountStatus.SUSPENDED);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountClose.execute(bankingAccount.getId(), request)
        );

        // then
        assertThat(exception).hasMessage(ErrorCodes.BANKING_ACCOUNT_SUSPENDED);
    }

    @Test
    @DisplayName("Should throws when BankingAccount is not found")
    void closeAccount_WhenAccountNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountClose.execute(bankingAccount.getId(), request)
        );

        // then
        assertTrue(exception.getMessage().contains(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should throws exception when authenticated customer is not the owner of the account")
    void closeAccount_WhenAccountNotOwnedByCustomer_ThrowsException() {
        // given
        Customer customer2 = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customer2@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customer2);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountNotOwnerException exception = assertThrows(
                BankingAccountNotOwnerException.class,
                () -> bankingAccountClose.execute(bankingAccount.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER);
    }

    @Test
    @DisplayName("Should returns a closed BankingAccount when not owner but it is admin")
    void closeAccount_WhenAccountNotOwnedByCustomerButItIsAdmin_ThrowsException() {
        // given
        Customer customerAdmin = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setRole(UserAccountRole.ADMIN)
                           .setEmail("customerAdmin@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customerAdmin);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(bankingAccount);

        BankingAccount result = bankingAccountClose.execute(
                bankingAccount.getId(), request
        );

        // then
        assertThat(result.getStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

}