package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountAliasUpdateRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCloseRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountOpenRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingAccountManagementServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankingAccountManagementService bankingAccountManagementService;

    @Test
    @DisplayName("Should close a customer BankingAccount")
    void shouldCloseAccount() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingAccountManagementService.closeAccount(
                givenBankingAccount.getId(),
                request
        );

        // then
        assertThat(givenBankingAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository).findById(givenBankingAccount.getId());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should not open BankingAccount When its closed")
    void shouldFailToOpenAccountWhenClosed() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setStatus(BankingAccountStatus.CLOSED);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccountManagementService.openAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_CLOSED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not open BankingAccount When its suspended")
    void shouldFailToOpenAccountWhenSuspended() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountManagementService.openAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not close BankingAccount when is suspended")
    void shouldFailToCloseAccountWhenSuspended() {
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
        givenBankingAccount.setStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountManagementService.closeAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertTrue(exception.getMessage().contains("suspended"));
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
                () -> bankingAccountManagementService.closeAccount(givenBankingAccount.getId(), request)
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
                () -> bankingAccountManagementService.closeAccount(givenBankingAccount.getId(), request)
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

        BankingAccount savedAccount = bankingAccountManagementService.closeAccount(
                givenBankingAccount.getId(), request
        );

        // then
        assertThat(savedAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should set alias to a logged customer BankingAccount")
    void shouldSetAliasToAccount() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
                "account for savings"
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingAccountManagementService.setAccountAlias(
                givenBankingAccount.getId(),
                request
        );


        // then
        assertThat(givenBankingAccount.getAlias()).isEqualTo(request.alias());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }
}
