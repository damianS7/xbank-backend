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
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BankingAccountCloseTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

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
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customer.setBankingAccounts(Set.of(bankingAccount));
    }

    @Test
    @DisplayName("Should close a customer BankingAccount")
    void execute_Valid_ReturnsClosedBankingAccount() {
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
        Assertions.assertThat(bankingAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository).findById(bankingAccount.getId());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should close a customer BankingAccount")
    void shouldCloseAccount() {
        // given
        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingAccountRepository.findById(bankingAccount.getId())).thenReturn(Optional.of(bankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingAccountClose.execute(
                bankingAccount.getId(),
                request
        );

        // then
        assertThat(bankingAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository).findById(bankingAccount.getId());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should not close BankingAccount when is suspended")
    void shouldFailToCloseAccountWhenSuspended() {
        // given
        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

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
    @DisplayName("Should not close BankingAccount when none is found")
    void shouldFailToCloseAccountWhenNotFound() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountClose.execute(givenBankingAccount.getId(), request)
        );

        // then
        assertTrue(exception.getMessage().contains(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should not close account if you are not the owner and you are not admin either")
    void shouldFailToCloseAccountWhenItsNotYoursAndYouAreNotAdmin() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        // given
        Customer customer2 = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customer2@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        final String accountNumber = "US99 0000 1111 1122 3333 4444";

        BankingAccount givenBankingAccount = new BankingAccount(customer2);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountNotOwnerException exception = assertThrows(
                BankingAccountNotOwnerException.class,
                () -> bankingAccountClose.execute(givenBankingAccount.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER);
    }

    @Test
    @DisplayName("Should close an account even if its not yours when you are ADMIN")
    void shouldCloseBankingAccountWhenYouAreAdmin() {
        // given
        Customer customerAdmin = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setRole(UserAccountRole.ADMIN)
                           .setEmail("customerAdmin@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customerAdmin);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        final String accountNumber = "US99 0000 1111 1122 3333 4444";

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);

        BankingAccount savedAccount = bankingAccountClose.execute(
                givenBankingAccount.getId(), request
        );

        // then
        assertThat(savedAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

}