package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infra.repository.BankingTransactionRepository;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingAccountOperationService bankingAccountOperationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private BankingTransactionAccountService bankingTransactionAccountService;

    @Test
    @DisplayName("Should transfer to")
    void shouldTransferTo() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BigDecimal fromCustomerAccountInitialBalance = BigDecimal.valueOf(1000);

        BankingAccount fromCustomerAccount = new BankingAccount(fromCustomer);
        fromCustomerAccount.setId(2L);
        fromCustomerAccount.setBalance(fromCustomerAccountInitialBalance);
        fromCustomerAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerAccount = new BankingAccount(toCustomer);
        toCustomerAccount.setId(5L);
        toCustomerAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                toCustomerAccount.getAccountNumber(),
                "a gift!",
                fromCustomerAccount.getBalance(),
                RAW_PASSWORD
        );

        BankingTransaction transaction = BankingTransaction
                .create()
                .setAssociatedBankingAccount(fromCustomerAccount)
                .setTransactionType(BankingTransactionType.TRANSFER_TO)
                .setLastBalance(BigDecimal.ZERO)
                .setAmount(transferRequest.amount())
                .setDescription(transferRequest.description());

        // when
        when(bankingAccountRepository.findById(fromCustomerAccount.getId())).thenReturn(Optional.of(
                fromCustomerAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerAccount));

        when(bankingTransactionAccountService.createTransaction(
                any(BankingAccount.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(transaction);

        when(bankingTransactionAccountService.persistTransaction(
                any(BankingTransaction.class)
        )).thenReturn(transaction);

        doNothing().when(notificationService).publishNotification(any(NotificationEvent.class));

        // then
        transaction = bankingAccountOperationService.transfer(
                fromCustomerAccount.getId(),
                transferRequest
        );

        // then
        assertThat(transaction)
                .isNotNull()
                .extracting(
                        BankingTransaction::getId,
                        BankingTransaction::getLastBalance,
                        BankingTransaction::getAmount,
                        BankingTransaction::getStatus
                )
                .containsExactly(
                        transaction.getId(),
                        transaction.getLastBalance(),
                        transaction.getAmount(),
                        transaction.getStatus()
                );

        assertThat(fromCustomerAccount.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        assertThat(toCustomerAccount.getBalance()).isEqualTo(fromCustomerAccountInitialBalance);
    }

    @Test
    @DisplayName("Should fail to transfer when insufficient funds")
    void shouldFailToTransferWhenInsufficientFunds() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(100),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));
        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerBankingAccount));

        // then
        BankingAccountInsufficientFundsException exception = assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> bankingAccountOperationService.transfer(
                        fromCustomerBankingAccount.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when destination account has different currency")
    void shouldFailToTransferWhenDestinationAccountHasDifferentCurrency() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setAccountCurrency(BankingAccountCurrency.USD);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(100),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerBankingAccount));

        // then
        BankingAccountTransferCurrencyMismatchException exception = assertThrows(
                BankingAccountTransferCurrencyMismatchException.class,
                () -> bankingAccountOperationService.transfer(
                        fromCustomerBankingAccount.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.TRANSACTION.DIFFERENT_CURRENCY, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when account is not found")
    void shouldFailToTransferWhenAccountIsNotFound() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        //        setUpContext(customer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setAccountCurrency(BankingAccountCurrency.USD);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.empty());

        // then
        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountOperationService.transfer(
                        fromCustomerBankingAccount.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when account is closed")
    void shouldFailToTransferWhenAccountIsClosed() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setAccountStatus(BankingAccountStatus.CLOSED);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(0));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(50),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerBankingAccount));

        // then
        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccountOperationService.transfer(
                        fromCustomerBankingAccount.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.CLOSED, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when account is suspended")
    void shouldFailToTransferWhenAccountIsSuspended() {
        // given
        Customer customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(customer);

        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setAccountStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccountA.setBalance(BigDecimal.valueOf(1000));
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(1000),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerBankingAccount));

        // then
        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer into same account")
    void shouldFailToTransferIntoSameAccount() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount givenBankingAccountA = new BankingAccount(fromCustomer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(BigDecimal.valueOf(1000));
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                givenBankingAccountA.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(1000),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));

        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountA.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountA));

        // then
        BankingAccountTransferSameAccountException exception = assertThrows(
                BankingAccountTransferSameAccountException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.SAME_DESTINATION, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when account its not yours")
    void shouldFailToTransferWhenAccountItsNotYours() {
        // given
        Customer loggedCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(loggedCustomer);

        Customer randomCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount randomCustomerBankingAccount = new BankingAccount(randomCustomer);
        randomCustomerBankingAccount.setId(2L);
        randomCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        randomCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        // Here we try to transfer from an account that does not belong to the logged customer
        BankingAccountTransferRequest transferRequest = new BankingAccountTransferRequest(
                randomCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(randomCustomerBankingAccount.getId())).thenReturn(Optional.of(
                randomCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(randomCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(randomCustomerBankingAccount));

        // then
        BankingAccountOwnershipException exception = assertThrows(
                BankingAccountOwnershipException.class,
                () -> bankingAccountOperationService.transfer(
                        randomCustomerBankingAccount.getId(),
                        transferRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.OWNERSHIP, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to transfer when password is wrong")
    void shouldFailToTransferWhenPasswordIsWrong() {
        // given
        Customer fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        setUpContext(fromCustomer);

        BankingAccount fromCustomerBankingAccount = new BankingAccount(fromCustomer);
        fromCustomerBankingAccount.setId(2L);
        fromCustomerBankingAccount.setBalance(BigDecimal.valueOf(1000));
        fromCustomerBankingAccount.setAccountNumber("US9900001111112233334444");

        Customer toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerB@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        BankingAccount toCustomerBankingAccount = new BankingAccount(toCustomer);
        toCustomerBankingAccount.setId(5L);
        toCustomerBankingAccount.setBalance(BigDecimal.valueOf(100));
        toCustomerBankingAccount.setAccountNumber("ES0400003110112293532124");

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                toCustomerBankingAccount.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                "WRONG_PASSWORD"
        );

        when(bankingAccountRepository.findById(fromCustomerBankingAccount.getId())).thenReturn(Optional.of(
                fromCustomerBankingAccount));

        when(bankingAccountRepository.findByAccountNumber(toCustomerBankingAccount.getAccountNumber()))
                .thenReturn(Optional.of(toCustomerBankingAccount));

        // then
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingAccountOperationService.transfer(
                        fromCustomerBankingAccount.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.USER.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

}