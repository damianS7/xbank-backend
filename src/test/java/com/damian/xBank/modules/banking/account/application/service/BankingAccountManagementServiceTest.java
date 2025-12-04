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
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountOwnershipException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infra.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
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
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);

        BankingAccount savedAccount = bankingAccountManagementService.closeAccount(
                givenBankingAccount.getId(),
                request
        );

        // then
        assertThat(savedAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }

    @Test
    @DisplayName("Should not open BankingAccount When its closed")
    void shouldNotOpenAccountWhenClosed() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountStatus(BankingAccountStatus.CLOSED);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccountManagementService.openAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.CLOSED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not open BankingAccount When its suspended")
    void shouldNotOpenAccountWhenSuspended() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountOpenRequest request = new BankingAccountOpenRequest();

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountManagementService.openAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not close BankingAccount When is suspended")
    void shouldNotCloseAccountWhenSuspended() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
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
    @DisplayName("Should not close BankingAccount When none is found")
    void shouldNotCloseAccountWhenNotFound() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        final String accountNumber = "US99 0000 1111 1122 3333 4444";
        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountManagementService.closeAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertTrue(exception.getMessage().contains(
                Exceptions.BANKING.ACCOUNT.NOT_FOUND
        ));
    }

    //
    @Test
    @DisplayName("Should not close account if you are not the owner and you are not admin either")
    void shouldNotCloseAccountWhenItsNotYoursAndYouAreNotAdmin() {
        // given
        UserAccount userAccount2 = UserAccount.create()
                                              .setId(2L)
                                              .setEmail("customer2@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer2 = Customer.create()
                                     .setId(2L)
                                     .setAccount(userAccount2);

        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);


        setUpContext(customer);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        final String accountNumber = "US99 0000 1111 1122 3333 4444";

        BankingAccount givenBankingAccount = new BankingAccount(customer2);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber(accountNumber);

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));

        BankingAccountOwnershipException exception = assertThrows(
                BankingAccountOwnershipException.class,
                () -> bankingAccountManagementService.closeAccount(givenBankingAccount.getId(), request)
        );

        // then
        assertTrue(exception.getMessage().contains(
                Exceptions.BANKING.ACCOUNT.ACCESS_FORBIDDEN
        ));
    }

    @Test
    @DisplayName("Should close an account even if its not yours when you are ADMIN")
    void shouldCloseBankingAccountWhenYouAreAdmin() {
        // given
        UserAccount userAdminAccount = UserAccount.create()
                                                  .setId(2L)
                                                  .setRole(UserAccountRole.ADMIN)
                                                  .setEmail("customer2@demo.com")
                                                  .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerAdmin = Customer.create()
                                         .setId(2L)
                                         .setAccount(userAdminAccount);

        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);


        setUpContext(customerAdmin);

        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
                RAW_PASSWORD
        );

        final String accountNumber = "US99 0000 1111 1122 3333 4444";

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
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
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
                "account for savings"
        );

        BankingAccount givenBankingAccount = new BankingAccount(customer);
        givenBankingAccount.setId(5L);
        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
        givenBankingAccount.setAccountNumber("US9900001111112233334444");

        // when
        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);

        BankingAccount savedAccount = bankingAccountManagementService.setAccountAlias(
                givenBankingAccount.getId(),
                request
        );

        // then
        assertThat(savedAccount.getAlias()).isEqualTo(request.alias());
        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    }
}
