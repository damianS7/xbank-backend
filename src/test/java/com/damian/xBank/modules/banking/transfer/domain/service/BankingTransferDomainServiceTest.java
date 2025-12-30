package com.damian.xBank.modules.banking.transfer.domain.service;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
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

public class BankingTransferDomainServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingTransferDomainService bankingTransferDomainService;

    @Mock
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

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
                .create(fromCustomer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        toCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("toCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        toAccount = BankingAccount
                .create(toCustomer)
                .setId(2L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US1200001111112233335555");
    }

    @Test
    @DisplayName("createTransfer should successfully create a transfer when all inputs are valid")
    void createTransfer_Valid_ReturnsTransfer() {
        // given
        BigDecimal givenAmount = BigDecimal.valueOf(100);
        String givenDescription = "a gift!";

        // when
        //        when(bankingTransferRepository.save(any(BankingTransfer.class))).thenAnswer(
        //                i -> i.getArguments()[0]
        //        );

        // then
        BankingTransfer resultTransfer = bankingTransferDomainService.createTransfer(
                fromCustomer.getId(),
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
    }

    @Test
    @DisplayName("createTransfer should throw exception when the customer is not the owner of the account")
    void createTransfer_BankingAccountNotOwner_ThrowsException() {
        // given
        // when
        // then
        BankingAccountNotOwnerException exception = assertThrows(
                BankingAccountNotOwnerException.class,
                () -> bankingTransferDomainService.createTransfer(
                        toCustomer.getId(),
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
        // when
        // then
        BankingAccountInsufficientFundsException exception = assertThrows(
                BankingAccountInsufficientFundsException.class,
                () -> bankingTransferDomainService.createTransfer(
                        fromCustomer.getId(),
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
        toAccount.setCurrency(BankingAccountCurrency.USD);

        // when
        // then
        BankingTransferCurrencyMismatchException exception = assertThrows(
                BankingTransferCurrencyMismatchException.class,
                () -> bankingTransferDomainService.createTransfer(
                        fromCustomer.getId(),
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
        toAccount.setStatus(BankingAccountStatus.CLOSED);

        // when
        // then
        BankingAccountClosedException exception = assertThrows(
                BankingAccountClosedException.class,
                () -> bankingTransferDomainService.createTransfer(
                        fromCustomer.getId(),
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
        toAccount.setStatus(BankingAccountStatus.SUSPENDED);

        // when
        // then
        BankingAccountSuspendedException exception = assertThrows(
                BankingAccountSuspendedException.class,
                () -> bankingTransferDomainService.createTransfer(
                        fromCustomer.getId(),
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
        // when
        // then
        BankingTransferSameAccountException exception = assertThrows(
                BankingTransferSameAccountException.class,
                () -> bankingTransferDomainService.createTransfer(
                        fromCustomer.getId(),
                        fromAccount,
                        fromAccount,
                        BigDecimal.valueOf(1),
                        "a gift!"
                )
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT);
    }

    @Test
    @DisplayName("confirmTransfer should successfully confirm a transfer")
    void confirmTransfer_Valid_ReturnsConfirmedTransfer() {
        // given
        BigDecimal fromCustomerAccountInitialBalance = BigDecimal.valueOf(1000);
        fromAccount.setBalance(fromCustomerAccountInitialBalance);

        BigDecimal toCustomerAccountInitialBalance = BigDecimal.valueOf(0);
        toAccount.setBalance(toCustomerAccountInitialBalance);

        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);

        BankingTransfer givenTransfer = BankingTransfer
                .create(fromAccount, toAccount, givenTransferAmount)
                .setId(1L)
                .setDescription("a gift!");

        BankingTransaction fromTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_TO,
                        fromAccount,
                        givenTransferAmount
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(givenTransfer.getDescription());

        BankingTransaction toTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        givenTransferAmount
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(givenTransfer.getDescription());

        givenTransfer.addTransaction(fromTransaction);
        givenTransfer.addTransaction(toTransaction);

        // then
        BankingTransfer resultTransfer = bankingTransferDomainService.confirmTransfer(
                fromCustomer.getId(),
                givenTransfer
        );

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

        //        verify(bankingAccountRepository, times(2)).save(any(BankingAccount.class));
        //        verify(bankingTransferRepository, times(1)).save(any(BankingTransfer.class));
    }
}