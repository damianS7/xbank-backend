package com.damian.xBank.modules.banking.transfer.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.enums.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameException;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransferServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingTransferService bankingTransferService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransferRepository bankingTransferRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @Mock
    private BankingTransactionService bankingTransactionService;

    private Customer fromCustomer;
    private Customer toCustomer;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;

    @BeforeEach
    void setUp() {
        fromCustomer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        fromAccount = BankingAccount
                .create()
                .setOwner(fromCustomer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        toAccount = BankingAccount
                .create()
                .setOwner(toCustomer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("createTransfer should successfully create a transfer when all inputs are valid")
    void createTransfer_Valid_ReturnsTransfer() {
        // given
        setUpContext(fromCustomer);

        BigDecimal givenAmount = BigDecimal.valueOf(100);
        String givenDescription = "a gift!";

        // when
        when(bankingTransferRepository.save(any(BankingTransfer.class))).thenAnswer(
                i -> i.getArguments()[0]
        );

        // then
        BankingTransfer resultTransfer = bankingTransferService.createTransfer(
                fromAccount,
                toAccount,
                givenAmount,
                givenDescription
        );

        // then
        assertThat(resultTransfer)
                .isNotNull()
                .extracting(
                        BankingTransfer::getId,
                        BankingTransfer::getAmount,
                        BankingTransfer::getStatus,
                        BankingTransfer::getDescription,
                        BankingTransfer::getCreatedAt
                )
                .containsExactly(
                        resultTransfer.getId(),
                        givenAmount,
                        BankingTransferStatus.PENDING,
                        givenDescription,
                        resultTransfer.getCreatedAt()
                );

        BankingTransaction fromTx = resultTransfer.getTransactions().stream()
                                                  .filter(tx -> tx.getBankingAccount().equals(fromAccount))
                                                  .findFirst()
                                                  .orElseThrow();
        assertEquals(BankingTransactionType.TRANSFER_TO, fromTx.getType());
        assertEquals(givenAmount, fromTx.getAmount());
        assertEquals(fromAccount.getBalance().subtract(givenAmount), fromTx.getBalanceAfter());

        BankingTransaction toTx = resultTransfer.getTransactions().stream()
                                                .filter(tx -> tx.getBankingAccount().equals(toAccount))
                                                .findFirst()
                                                .orElseThrow();
        assertEquals(BankingTransactionType.TRANSFER_FROM, toTx.getType());
        assertEquals(givenAmount, toTx.getAmount());
        assertEquals(toAccount.getBalance().add(givenAmount), toTx.getBalanceAfter());

        assertEquals(2, resultTransfer.getTransactions().size());
        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }

    @Test
    @DisplayName("createTransfer should throw exception when the customer is not the owner of the account")
    void createTransfer_BankingAccountNotOwner_ThrowsException() {
        // given
        setUpContext(toCustomer);

        // when
        // then
        BankingAccountNotOwnerException exception = assertThrows(
                BankingAccountNotOwnerException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        toAccount,
                        BigDecimal.valueOf(100),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER);
    }

    @Test
    @DisplayName("createTransfer should throw exception when insufficient funds")
    void createTransfer_InsufficientFunds_ThrowsException() {
        // given
        setUpContext(fromCustomer);

        // when
        // then
        BankingAccountInsufficientFundsException exception = assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        toAccount,
                        BigDecimal.valueOf(1000000),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("createTransfer should throw exception when accounts have different currencies")
    void createTransfer_DifferentCurrencies_ThrowsException() {
        // given
        setUpContext(fromCustomer);
        toAccount.setCurrency(BankingAccountCurrency.USD);

        // when
        // then
        BankingTransferCurrencyMismatchException exception = assertThrows(
                BankingTransferCurrencyMismatchException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        toAccount,
                        BigDecimal.valueOf(1),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY);
    }

    @Test
    @DisplayName("createTransfer should throw exception when account is closed")
    void createTransfer_AccountClosed_ThrowsException() {
        // given
        setUpContext(fromCustomer);
        toAccount.setStatus(BankingAccountStatus.CLOSED);

        // when
        // then
        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        toAccount,
                        BigDecimal.valueOf(1),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_ACCOUNT_CLOSED);
    }

    @Test
    @DisplayName("createTransfer should throw exception when account is suspended")
    void createTransfer_AccountSuspended_ThrowsException() {
        // given
        setUpContext(fromCustomer);
        toAccount.setStatus(BankingAccountStatus.SUSPENDED);

        // when
        // then
        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        toAccount,
                        BigDecimal.valueOf(1),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_ACCOUNT_SUSPENDED);
    }

    @Test
    @DisplayName("createTransfer should throw exception when both accounts are the same")
    void createTransfer_SameAccount_ThrowsException() {
        // given
        setUpContext(fromCustomer);

        // when
        // then
        BankingTransferSameException exception = assertThrows(
                BankingTransferSameException.class,
                () -> bankingTransferService.createTransfer(
                        fromAccount,
                        fromAccount,
                        BigDecimal.valueOf(1),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSFER_SAME_DESTINATION);
    }

    @Test
    @DisplayName("confirmTransfer")
    void confirmTransfer_Valid_ReturnsConfirmedTransfer() {
        // given
        BigDecimal fromCustomerAccountInitialBalance = BigDecimal.valueOf(1000);
        fromAccount.setBalance(fromCustomerAccountInitialBalance);

        BigDecimal toCustomerAccountInitialBalance = BigDecimal.valueOf(0);
        toAccount.setBalance(toCustomerAccountInitialBalance);

        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingTransfer givenTransfer = BankingTransfer
                .create()
                .setId(1L)
                .setFromAccount(fromAccount)
                .setToAccount(toAccount)
                .setAmount(givenTransferAmount)
                .setDescription("a gift!");

        BankingTransaction fromTransaction = BankingTransaction
                .create()
                .setBankingAccount(fromAccount)
                .setType(BankingTransactionType.TRANSFER_TO)
                .setBalanceBefore(fromCustomerAccountInitialBalance)
                .setAmount(givenTransferAmount)
                .setBalanceAfter(fromCustomerAccountInitialBalance.subtract(givenTransferAmount))
                .setDescription(givenTransfer.getDescription());

        BankingTransaction toTransaction = BankingTransaction
                .create()
                .setBankingAccount(toAccount)
                .setType(BankingTransactionType.TRANSFER_FROM)
                .setBalanceBefore(toCustomerAccountInitialBalance)
                .setAmount(givenTransferAmount)
                .setBalanceAfter(toCustomerAccountInitialBalance.add(givenTransferAmount))
                .setDescription(givenTransfer.getDescription());

        givenTransfer.addTransaction(fromTransaction);
        givenTransfer.addTransaction(toTransaction);

        // when
        when(bankingTransactionService.complete(any(BankingTransaction.class)))
                .thenAnswer(i -> {
                    BankingTransaction tx = i.getArgument(0);
                    tx.complete();
                    return tx;
                });

        when(bankingAccountRepository.save(any(BankingAccount.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(bankingTransferRepository.save(any(BankingTransfer.class)))
                .thenAnswer(i -> i.getArgument(0));

        // then
        BankingTransfer resultTransfer = bankingTransferService.confirmTransfer(givenTransfer);

        assertThat(resultTransfer)
                .isNotNull()
                .extracting(
                        BankingTransfer::getId,
                        BankingTransfer::getAmount,
                        BankingTransfer::getStatus,
                        BankingTransfer::getDescription,
                        BankingTransfer::getCreatedAt
                )
                .containsExactly(
                        givenTransfer.getId(),
                        givenTransferAmount,
                        BankingTransferStatus.CONFIRMED,
                        givenTransfer.getDescription(),
                        givenTransfer.getCreatedAt()
                );

        assertThat(resultTransfer.getTransactions())
                .hasSize(2);

        // check balance has been deducted
        assertThat(fromAccount.getBalance()).isEqualTo(
                fromCustomerAccountInitialBalance.subtract(givenTransferAmount)
        );

        // check balance has been added
        assertThat(toAccount.getBalance()).isEqualTo(
                toCustomerAccountInitialBalance.add(givenTransferAmount)
        );

        BankingTransaction fromTx = resultTransfer.getFromTransaction();
        assertThat(fromTx)
                .isNotNull()
                .extracting(
                        BankingTransaction::getType,
                        BankingTransaction::getStatus,
                        BankingTransaction::getBalanceBefore,
                        BankingTransaction::getAmount,
                        BankingTransaction::getBalanceAfter
                )
                .containsExactly(
                        BankingTransactionType.TRANSFER_TO,
                        BankingTransactionStatus.COMPLETED,
                        fromTransaction.getBalanceBefore(),
                        fromTransaction.getAmount(),
                        fromTransaction.getBalanceAfter()
                );

        BankingTransaction toTx = resultTransfer.getToTransaction();
        assertThat(toTx)
                .isNotNull()
                .extracting(
                        BankingTransaction::getType,
                        BankingTransaction::getStatus,
                        BankingTransaction::getBalanceBefore,
                        BankingTransaction::getAmount,
                        BankingTransaction::getBalanceAfter
                )
                .containsExactly(
                        BankingTransactionType.TRANSFER_FROM,
                        BankingTransactionStatus.COMPLETED,
                        toTransaction.getBalanceBefore(),
                        toTransaction.getAmount(),
                        toTransaction.getBalanceAfter()
                );

        verify(bankingAccountRepository, times(2)).save(any(BankingAccount.class));
        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }
}