package com.damian.xBank.modules.banking.account;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.application.service.BankingAccountOperationService;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
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
    private BankingTransactionAccountService bankingTransactionAccountService;

    @Test
    @DisplayName("Should transfer to")
    void shouldTransferTo() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        when(bankingTransactionAccountService.createTransaction(
                any(BankingAccount.class),
                any(BankingTransactionType.class),
                any(BigDecimal.class),
                any(String.class)
        )).thenReturn(givenBankingTransactionA);

        when(bankingTransactionAccountService.persistTransaction(
                any(BankingTransaction.class)
        )).thenReturn(givenBankingTransactionA);

        doNothing().when(notificationService).publishNotification(any(NotificationEvent.class));

        // then
        BankingTransaction transaction = bankingAccountOperationService.transfer(
                givenBankingAccountA.getId(),
                givenRequest
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(givenBankingAccountA.getBalance()).isEqualTo(
                givenBalanceAccountA.subtract(givenTransferAmount)
        );
        assertThat(givenBankingAccountB.getBalance()).isEqualTo(
                givenBalanceAccountB.add(givenTransferAmount)
        );
    }

    @Test
    @DisplayName("Should not transfer when insufficient funds")
    void shouldNotTransferWhenInsufficientFunds() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        BankingAccountInsufficientFundsException exception = assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not transfer when one account has different currency")
    void shouldNotTransferWhenAccountHasDifferentCurrency() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setAccountCurrency(BankingAccountCurrency.USD);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        BankingAccountTransferCurrencyMismatchException exception = assertThrows(
                BankingAccountTransferCurrencyMismatchException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.TRANSACTION.DIFFERENT_CURRENCY, exception.getMessage());
    }

    @Test
    @DisplayName("Should not transfer when account is not found")
    void shouldNotTransferWhenAccountIsNotFound() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        //        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setAccountCurrency(BankingAccountCurrency.USD);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.empty());

        // then
        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not transfer when account is closed")
    void shouldNotTransferWhenAccountIsClosed() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setAccountStatus(BankingAccountStatus.CLOSED);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.CLOSED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not transfer when account is suspended")
    void shouldNotTransferWhenAccountIsSuspended() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setAccountStatus(BankingAccountStatus.SUSPENDED);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("Should not transfer to itself")
    void shouldNotTransferToItself() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(1000);
        BankingAccount givenBankingAccountA = new BankingAccount(customer);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountA.getAccountNumber(),
                "a gift!",
                givenTransferAmount,
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
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.SAME_DESTINATION, exception.getMessage());
    }

    @Test
    @DisplayName("Should no transfer when account its not yours")
    void shouldNotTransferWhenAccountItsNotYours() {
        // given
        UserAccount userAccountA = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customer@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerA = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountA);

        setUpContext(customerA);

        UserAccount userAccountB = UserAccount.create()
                                              .setId(2L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(2L)
                                     .setAccount(userAccountB);


        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(10000);
        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);

        BankingAccount givenBankingAccountA = new BankingAccount(customerB);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                RAW_PASSWORD
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        BankingAccountOwnershipException exception = assertThrows(
                BankingAccountOwnershipException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.OWNERSHIP, exception.getMessage());
    }

    @Test
    @DisplayName("Should process transaction and fail to transfer when password is wrong")
    void shouldProcessTransactionRequestAndFailToTransferWhenPasswordIsWrong() {
        // given
        UserAccount userAccountA = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customer@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerA = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountA);

        setUpContext(customerA);

        BigDecimal givenBalanceAccountA = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountA = new BankingAccount(customerA);
        givenBankingAccountA.setId(2L);
        givenBankingAccountA.setBalance(givenBalanceAccountA);
        givenBankingAccountA.setAccountNumber("US9900001111112233334444");

        UserAccount userAccountB = UserAccount.create()
                                              .setId(1L)
                                              .setEmail("customerB@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(1L)
                                     .setAccount(userAccountB);

        BigDecimal givenBalanceAccountB = BigDecimal.valueOf(0);
        BankingAccount givenBankingAccountB = new BankingAccount(customerB);
        givenBankingAccountB.setId(5L);
        givenBankingAccountB.setBalance(givenBalanceAccountB);
        givenBankingAccountB.setAccountNumber("ES0400003110112293532124");

        BigDecimal givenTransferAmount = BigDecimal.valueOf(500);
        BankingTransaction givenBankingTransactionA = new BankingTransaction(givenBankingAccountA);
        givenBankingTransactionA.setAmount(givenTransferAmount);

        BankingAccountTransferRequest givenRequest = new BankingAccountTransferRequest(
                givenBankingAccountB.getAccountNumber(),
                "a gift!",
                BigDecimal.valueOf(500),
                "WRONG_PASSWORD"
        );

        when(bankingAccountRepository.findById(givenBankingAccountA.getId())).thenReturn(Optional.of(
                givenBankingAccountA));
        when(bankingAccountRepository.findByAccountNumber(givenBankingAccountB.getAccountNumber()))
                .thenReturn(Optional.of(givenBankingAccountB));

        // then
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingAccountOperationService.transfer(
                        givenBankingAccountA.getId(),
                        givenRequest
                )
        );

        // then
        assertEquals(Exceptions.USER.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

}